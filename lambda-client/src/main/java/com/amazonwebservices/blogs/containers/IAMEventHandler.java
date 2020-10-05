package com.amazonwebservices.blogs.containers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObject;
import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObjectList;
import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObjectSpec;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.CustomClientBuilder;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import io.vertx.core.json.JsonObject;

public class IAMEventHandler implements RequestStreamHandler {
	
	private static final Logger logger = LogManager.getLogger(IAMEventHandler.class);

	private final static String IAM_SOURCE = "iam.amazonaws.com";
	private final static String ADD_USER_TO_GROUP = "AddUserToGroup";
	private final static String REMOVE_USER_FROM_GROUP = "RemoveUserFromGroup";
	
	
	private ApiClient apiClient = null;
	private GenericKubernetesApi<IamUserGroupCustomObject, IamUserGroupCustomObjectList> apiIamGroupClient = null;
	private boolean isInitialized = false;
	
	public void initialize () throws IOException {
		logger.info("Intializing the API client");
		apiClient = CustomClientBuilder.custom();
		apiIamGroupClient = new GenericKubernetesApi<IamUserGroupCustomObject, IamUserGroupCustomObjectList> (
				IamUserGroupCustomObject.class, 
				IamUserGroupCustomObjectList.class, 
				"octank.com", 
				"v1", 
				"iamusergroups",
				apiClient);
	}
	
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		if (!isInitialized) {
			initialize();
			isInitialized = true;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
		StringBuilder sb = new StringBuilder();
		String nextLine;
		while ((nextLine = reader.readLine()) != null) {
			sb.append(nextLine);
		}
		String inputString = sb.toString();
		logger.debug(inputString);
		handle (inputString);
	}
	
	private void handle (String inputString) throws IOException {	
		JsonObject inputObject = new JsonObject (inputString);
		String account = inputObject.getString("account");
		String eventName = inputObject.getJsonObject("detail").getString("eventName");
		String eventSource = inputObject.getJsonObject("detail").getString("eventSource");
		String groupName = inputObject.getJsonObject("detail").getJsonObject("requestParameters").getString("groupName");
		String userName = inputObject.getJsonObject("detail").getJsonObject("requestParameters").getString("userName");
		String userArn = String.format("arn:aws:iam::%s:user/%s", account, userName);
		
		logger.info(String.format("Handling IAM event notification from '%s'", eventSource));
		if (!eventSource.equals(IAM_SOURCE)) return;
		
		String objName = userName.concat("-").concat(groupName).toLowerCase();
		String objNamespace = "kube-system";
		
		IamUserGroupCustomObject iamUserGroup =
				new IamUserGroupCustomObject()
				.apiVersion("octank.com/v1")
				.kind("IamUserGroup")
				.metadata(new V1ObjectMeta()
						.name(objName)
						.namespace(objNamespace))
				.spec(new IamUserGroupCustomObjectSpec()
						.iamUser(userArn)
	                    .username(userName)
	                    .group(groupName));
		logger.debug(iamUserGroup.toString());
	                    
		if (eventName.equals(ADD_USER_TO_GROUP)) {
			logger.info(String.format("Adding user '%s' to group '%s'", userName, groupName));
		    KubernetesApiResponse<IamUserGroupCustomObject> createResponse = apiIamGroupClient.create(iamUserGroup);
		    if (!createResponse.isSuccess()) {
		    	logger.error(String.format("Failed to create a IamGroup custom object '%s.%s'", objName, objNamespace));
		    	logger.error(String.format("Error status details:\n%s", createResponse.getStatus().getDetails().toString()));
		    }
		    else {
		    	logger.error(String.format("Successfully created IamGroup custom object '%s.%s'", objName, objNamespace));
		    }
		}
		else if (eventName.equals(REMOVE_USER_FROM_GROUP)) {
			logger.info(String.format("Removing user '%s' from group '%s'", userName, groupName));
		    KubernetesApiResponse<IamUserGroupCustomObject> createResponse = apiIamGroupClient.delete(objNamespace, objName);
		    if (!createResponse.isSuccess()) {
		    	logger.error(String.format("Failed to delete a IamGroup custom object '%s.%s'", objName, objNamespace));
		    	logger.error(String.format("Error status details:\n%s", createResponse.getStatus().getDetails().toString()));
		    }
		    else {
		    	logger.error(String.format("Successfully deleted IamGroup custom object '%s.%s'", objName, objNamespace));
		    }
		}
	}
	
	public static void main(String[] args) throws IOException {
		JsonObject config = getConfiguration(args);
		IAMEventHandler handler = new IAMEventHandler();
		if (!handler.isInitialized) {
			handler.initialize();
			handler.isInitialized = true;
		}
		handler.handle(config.toString());
	}
	
	private static JsonObject getConfiguration(String[] args) {
		if (args.length != 0) {
			String path = args[0];
			String cwd = Paths.get(".").toAbsolutePath().normalize().toString();
			try (Scanner scanner = new Scanner(new File(cwd, path)).useDelimiter("\\A")) {
				JsonObject config = new JsonObject(scanner.next());
				return config;
			}
			catch (Exception ex) {
				System.out.println(String.format("Exception while parsing the JSON input file %s", path));
				ex.printStackTrace();
			}
		}
		return null;
	}
}
