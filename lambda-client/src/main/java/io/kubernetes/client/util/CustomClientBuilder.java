package io.kubernetes.client.util;

import org.apache.commons.codec.binary.Base64;

import com.amazonwebservices.blogs.containers.config.EKSConfig;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;

public class CustomClientBuilder extends ClientBuilder {
	public static ApiClient custom() {
		CustomClientBuilder builder = new CustomClientBuilder();
		builder.setCertificateAuthority(Base64.decodeBase64(EKSConfig.getCertAuthority()));
		builder.setVerifyingSsl(true);
		builder.setBasePath(EKSConfig.getApiServer());
		builder.setAuthentication(new CustomAuthentication());
		return builder.build();
	}
}
