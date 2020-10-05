package io.kubernetes.client.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonwebservices.blogs.containers.config.AWSConfig;
import com.amazonwebservices.blogs.containers.config.EKSConfig;
import com.amazonwebservices.blogs.containers.sigv4.AWS4SignerBase;
import com.amazonwebservices.blogs.containers.sigv4.AWS4SignerForAuthorizationHeader;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import io.kubernetes.client.util.credentials.Authentication;

public class CustomAuthentication implements Authentication {
	
	private static final Logger logger = LogManager.getLogger(CustomAuthentication.class);

    public static final String ISO8601BasicFormat = "yyyyMMdd'T'HHmmss'Z'";
    public static final String SimpleDateFormat = "yyyyMMdd";
	
	@Override
	public void provide(ApiClient client) {
		String token = getEKSToken();
		new AccessTokenAuthentication(token).provide(client);
	}
	
	public Map<String,String> getSessionToken() {
		try {
			BasicAWSCredentials credentials = new BasicAWSCredentials(AWSConfig.getAccessKey(), AWSConfig.getSecretKey());
			AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
			AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
					.withCredentials(credentialsProvider)
					.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(AWSConfig.getSTSEndpoint(), AWSConfig.getRegion()))
					.build();

			AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest();
			assumeRoleRequest.setRoleArn(AWSConfig.getAssumedRole());
			assumeRoleRequest.setDurationSeconds(3600);
			assumeRoleRequest.setRoleSessionName("EKSGetTokenAuth");
			AssumeRoleResult assumeRoleResult = stsClient.assumeRole(assumeRoleRequest);
			Credentials sessionCredentials = assumeRoleResult.getCredentials();

			String accessKeyId = sessionCredentials.getAccessKeyId();
			String secretAccessKey = sessionCredentials.getSecretAccessKey();
			String sessionToken = sessionCredentials.getSessionToken();

			logger.info(String.format("Assumed Role ID = %s", assumeRoleResult.getAssumedRoleUser()));
			logger.info(String.format("Access Key ID = %s", accessKeyId));
			logger.info(String.format("Secret Access Key = %s", secretAccessKey));
			logger.info(String.format("Session Token = %s", sessionToken));
			
			Map<String,String> credentialsMap = new HashMap<String,String>();
			credentialsMap.put("awsAccessKey", accessKeyId);
			credentialsMap.put("awsSecretKey", secretAccessKey);
			credentialsMap.put("sessionToken", sessionToken);
			return credentialsMap;
		} catch (Exception ex) {
			logger.error(String.format("Exception occurred when assuming role %s; %s",  AWSConfig.getAssumedRole(), ex.getMessage()), ex);
		}
		return null;
	}
	
    public String getEKSToken() {
    	
    	// Get session token from STS
		Map<String,String> credentialsMap = getSessionToken();
    	String awsAccessKey = credentialsMap.get("awsAccessKey");
    	String awsSecretKey = credentialsMap.get("awsSecretKey");
    	String sessionToken = credentialsMap.get("sessionToken");
      
    	// the region-specific end point to the target object expressed in path style
        URL endpointUrl;
        String urlString;
        try {
        	urlString = String.format("https://sts.%s.amazonaws.com/", AWSConfig.getRegion());
            endpointUrl = new URL(urlString);
            logger.info(String.format("Making GET request to %s", urlString));
        } 
        catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        
        // Get the data string in the given format
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(ISO8601BasicFormat);
        SimpleDateFormat dateFormat = new SimpleDateFormat(SimpleDateFormat);
        dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        dateFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        Date now = new Date();
        String dateTimeStamp = dateTimeFormat.format(now);
        
        // Add the header
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-k8s-aws-id", EKSConfig.getClusterName());

        // Add operation query parameters
        Map<String, String> opQueryParameters = new HashMap<String, String>();
        opQueryParameters.put("Action", "GetCallerIdentity");
        opQueryParameters.put("Version", "2011-06-15");
        
        // Add authentication query parameters
        Map<String, String> authQueryParameters = new HashMap<String, String>();
        authQueryParameters.put("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
        authQueryParameters.put("X-Amz-Credential", String.format("%s/%s/%s/sts/aws4_request", awsAccessKey, dateFormat.format(now), AWSConfig.getRegion()));
        authQueryParameters.put("X-Amz-Date", dateTimeStamp);
        authQueryParameters.put("X-Amz-Expires", "60");
        authQueryParameters.put("X-Amz-Security-Token", sessionToken);
        authQueryParameters.put("X-Amz-SignedHeaders", "host;x-k8s-aws-id");

        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(endpointUrl, "GET", "sts", AWSConfig.getRegion());
        Map<String,String> signingArtifacts = signer.computeSignature(
        		headers, 
        		opQueryParameters, 
        		authQueryParameters,
        		AWS4SignerBase.EMPTY_BODY_SHA256, 
        		awsAccessKey, 
        		awsSecretKey);
        
        String signedUrl = String.format("%s?%s&X-Amz-Signature=%s", 
        		urlString, 
        		signingArtifacts.get("QueryParameters"), 
        		signingArtifacts.get("Signature"));
        
        
        ByteBuffer utf8Buffer = StandardCharsets.UTF_8.encode(signedUrl); 
        ByteBuffer base64Buffer = Base64.getEncoder().encode(utf8Buffer);
        String utf8EncodedSignedUrl = StandardCharsets.UTF_8.decode(base64Buffer).toString();
        utf8EncodedSignedUrl = utf8EncodedSignedUrl.replace("=", "");
        String eksToken = EKSConfig.getEKSTokenPrefix().concat(utf8EncodedSignedUrl);
        logger.info(String.format("EKS Token =\n%s", eksToken));
        return eksToken;
    }
}
