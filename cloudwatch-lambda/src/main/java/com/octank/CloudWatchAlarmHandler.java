package com.octank;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ListTagsForResourceRequest;
import com.amazonaws.services.cloudwatch.model.ListTagsForResourceResult;
import com.amazonaws.services.cloudwatch.model.Tag;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.octank.kubernetes.model.CloudWatchAlarmCustomObject;
import com.octank.kubernetes.model.CloudWatchAlarmCustomObjectList;
import com.octank.kubernetes.model.CloudWatchAlarmCustomObjectSpec;
import com.octank.kubernetes.model.ScalingBehavior;
import com.octank.kubernetes.model.ScalingPolicy;

import io.kubernetes.client.extended.generic.GenericKubernetesApi;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.util.CustomClientBuilder;
import io.vertx.core.json.JsonObject;

public class CloudWatchAlarmHandler implements RequestHandler<SNSEvent, Object> {
	
	private static final Logger logger = Logger.getLogger(CloudWatchAlarmHandler.class);
	
	private enum ComparisonOperator {GreaterThanOrEqualToThreshold, GreaterThanThreshold, LessThanOrEqualToThreshold, LessThanThreshold};

	private final static String K8S_NAME = "kubernetes-name";
	private final static String K8S_NAMESPACE = "kubernetes-namespace";
	
	private final static String AWS_REGION = "us-east-1";
	

	final AmazonCloudWatch cloudWatchClient = AmazonCloudWatchClientBuilder.defaultClient();

	private ApiClient apiClient = null;
	private GenericKubernetesApi<CloudWatchAlarmCustomObject, CloudWatchAlarmCustomObjectList> apiCloudWatchAlarm = null;
	private GenericKubernetesApi<V1Deployment, V1DeploymentList> apiDeployment = null;
	private boolean isInitialized = false;
	
	public void initialize () {
		try {
		logger.info("Intializing the API client");
		apiClient = CustomClientBuilder.custom();
		
		this.apiCloudWatchAlarm = new GenericKubernetesApi<CloudWatchAlarmCustomObject, CloudWatchAlarmCustomObjectList>(
				CloudWatchAlarmCustomObject.class, 
				CloudWatchAlarmCustomObjectList.class,
				"octank.com", 
				"v1", 
				"cloudwatchalarms", 
				apiClient);
		
		this.apiDeployment = new GenericKubernetesApi<V1Deployment, V1DeploymentList>(
				V1Deployment.class, 
				V1DeploymentList.class,
				"apps", 
				"v1", 
				"deployments", 
				apiClient);
		}
		catch (Exception ex) {
			logger.error("Exception initializating the Kubernetes API client", ex);
		}
	}
	
	@Override
	public Object handleRequest(SNSEvent input, Context context) {
		if (!isInitialized) {
			initialize();
			isInitialized = true;
		}
		
		for (SNSRecord record : input.getRecords()) {
		
			//
			// Get the ARN of the CloudWatch alarm that triggered the SNS notification
			//
			SNS event = record.getSNS();
			String alarmMessage = event.getMessage();
			JsonObject alarmMessageObject = new JsonObject (alarmMessage);
			processCloudWatchAlarmMessage(alarmMessageObject);
		}
		
		return null;
	}

	private void processCloudWatchAlarmMessage (JsonObject alarmMessageObject) {
		String alarmName = alarmMessageObject.getString("AlarmName");
		String accountID = alarmMessageObject.getString("AWSAccountId");
		String alarmArn = String.format("arn:aws:cloudwatch:%s:%s:alarm:%s", AWS_REGION, accountID, alarmName);
		ComparisonOperator operator = Enum.valueOf(ComparisonOperator.class, alarmMessageObject.getJsonObject("Trigger").getString("ComparisonOperator"));
		
		//
		// Get the name/namespace of the CloudWatchAlarm Kubernetes custom resource from the tags associated with the CloudWatch alarm
		//
		ListTagsForResourceRequest request = new ListTagsForResourceRequest().withResourceARN(alarmArn);
		ListTagsForResourceResult response = cloudWatchClient.listTagsForResource(request);
		List<Tag> tags = response.getTags();
		String resourceName = null;
		String resoueceNamespace = null;
		for (Tag t : tags) {
			switch (t.getKey()) {
				case K8S_NAME:
					resourceName = t.getValue();
					break;
				case K8S_NAMESPACE:
					resoueceNamespace = t.getValue();
					break;
				default:
					break;
			}
		}
		if (resourceName == null || resoueceNamespace == null) {
			logger.error(String.format("Unable to identify the Kubernetes name and namespace of the CloudWatchAlarm custom resource for alarm '%s'", alarmName));
			return;
		}
		
		//
		// Fetch the CloudWatchAlarm Kubernetes custom resource from the API server
		// The custom resource contains the name of the Kubernetes Deployment resource to be scaled
		//
		CloudWatchAlarmCustomObject cloudWatchAlarm = apiCloudWatchAlarm.get(resoueceNamespace, resourceName).getObject();
		CloudWatchAlarmCustomObjectSpec cloudWatchAlarmSpec = cloudWatchAlarm.getSpec();
		int minReplicas = cloudWatchAlarmSpec.getMinReplicas();
		int maxReplicas = cloudWatchAlarmSpec.getMaxReplicas();
		ScalingBehavior scaleUpBehavior = cloudWatchAlarmSpec.getScaleUpBehavior();
		ScalingBehavior scaleDownBehavior = cloudWatchAlarmSpec.getScaleDownBehavior();
		String deploymentName = cloudWatchAlarmSpec.getDeployment();
		
		//
		// Fetch the Kubernetes Deployment resource from the API server
		// Compute the number of replicas to be scaled up or down
		// Update the Kubernetes Deployment resource with the new number of replicas.
		//
		V1Deployment deployment = apiDeployment.get(resoueceNamespace, deploymentName).getObject();
		int replicas = deployment.getSpec().getReplicas();
		int scaledReplicas = computeScaling(operator, minReplicas, maxReplicas, replicas, scaleUpBehavior, scaleDownBehavior);
		logger.info(String.format("Scaling the number of replicas for Deployment '%s.%s' from %d to %d", resoueceNamespace, deploymentName, replicas, scaledReplicas));
		deployment.getSpec().replicas(scaledReplicas);
		apiDeployment.update(deployment);
		logger.info("Scaling completed successfully");
	}
	

	private int computeScaling (ComparisonOperator operator, 
			int minReplicas, 
			int maxReplicas, 
			int replicas, 
			ScalingBehavior scaleUpBehavior, 
			ScalingBehavior scaleDownBehavior) {
		
		ScalingBehavior behavior = null;
		boolean scalingUp = true;
		
		if (Objects.equals(operator, ComparisonOperator.GreaterThanOrEqualToThreshold) ||
			Objects.equals(operator, ComparisonOperator.GreaterThanThreshold)) {
			behavior = scaleUpBehavior;
			scalingUp = true;
		}
		else if (Objects.equals(operator, ComparisonOperator.LessThanOrEqualToThreshold) ||
				 Objects.equals(operator, ComparisonOperator.LessThanThreshold)) {
			behavior = scaleDownBehavior;
			scalingUp = false;
		}
		
		List<ScalingPolicy> policies = behavior.getPolicies();
		int changeInReplicas = Integer.MIN_VALUE;
		for (ScalingPolicy policy : policies) {
			if (Objects.equals(policy.getType(), ScalingPolicy.ScalingType.Pods)) {
				int value = policy.getValue();
				if (value >= changeInReplicas) changeInReplicas = value;
			}
			else if (Objects.equals(policy.getType(), ScalingPolicy.ScalingType.Percent)) {
				int value = policy.getValue() * maxReplicas / 100;
				if (value >= changeInReplicas) changeInReplicas = value;
			}
		}
		
		int scaledReplicas = -1;
		if (scalingUp) {
			scaledReplicas = replicas + changeInReplicas;
			if (scaledReplicas > maxReplicas) scaledReplicas = maxReplicas;
		}
		else {
			scaledReplicas = replicas - changeInReplicas;
			if (scaledReplicas < minReplicas) scaledReplicas = minReplicas;
		}
		return scaledReplicas;
	}
	
	
	public static void main(String[] args) throws IOException {
		JsonObject config = getConfiguration(args);
		CloudWatchAlarmHandler handler = new CloudWatchAlarmHandler();
		if (!handler.isInitialized) {
			handler.initialize();
			handler.isInitialized = true;
		}
		handler.processCloudWatchAlarmMessage(config);
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