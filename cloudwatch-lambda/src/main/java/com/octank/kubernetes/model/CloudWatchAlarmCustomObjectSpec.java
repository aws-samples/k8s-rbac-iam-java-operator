package com.octank.kubernetes.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "CloudWatchAlarmCustomObjectSpec describes how a user wants their resource to appear")
public class CloudWatchAlarmCustomObjectSpec {
	public static final String SERIALIZED_MIN_REPLICAS = "minReplicas";
	@SerializedName(SERIALIZED_MIN_REPLICAS)
	private int minReplicas;
	
	public static final String SERIALIZED_MAX_REPLICAS = "maxReplicas";
	@SerializedName(SERIALIZED_MAX_REPLICAS)
	private int maxReplicas;
	
	public static final String SERIALIZED_DEPLOYMENT = "deployment";
	@SerializedName(SERIALIZED_DEPLOYMENT)
	private String deployment;
	
	public static final String SERIALIZED_SCALEUP_BEHAVIOR = "scaleUpBehavior";
	@SerializedName(SERIALIZED_SCALEUP_BEHAVIOR)
	private ScalingBehavior scaleUpBehavior;
	
	public static final String SERIALIZED_SCALEDOWN_BEHAVIOR = "scaleDownBehavior";
	@SerializedName(SERIALIZED_SCALEDOWN_BEHAVIOR)
	private ScalingBehavior scaleDownBehavior;

	public static final String SERIALIZED_COFIG = "config";
	@SerializedName(SERIALIZED_COFIG)
	private String config;
	
	//
	// Minimum replicas
	//
	@ApiModelProperty(required = true, value = "Minimum replicas")
	public int getMinReplicas() {
		return minReplicas;
	}

	public void setMinReplicas(int minReplicas) {
		this.minReplicas = minReplicas;
	}
	
	public CloudWatchAlarmCustomObjectSpec minReplicas(int minReplicas) {
		this.minReplicas = minReplicas;
		return this;
	}
	
	//
	// Maximum replicas
	//
	@ApiModelProperty(required = true, value = "Maximum replicas")
	public int getMaxReplicas() {
		return maxReplicas;
	}

	public void setMaxReplicas(int maxReplicas) {
		this.maxReplicas = maxReplicas;
	}
	
	public CloudWatchAlarmCustomObjectSpec maxReplicas(int maxReplicas) {
		this.maxReplicas = maxReplicas;
		return this;
	}
	
	//
	// Scaleup Behavior
	//
	@ApiModelProperty(required = true, value = "Scaleup Behavior")
	public ScalingBehavior getScaleUpBehavior() {
		return scaleUpBehavior;
	}

	public void setScaleUpBehavior(ScalingBehavior behavior) {
		this.scaleUpBehavior = behavior;
	}
	
	public CloudWatchAlarmCustomObjectSpec scaleUpBehavior(ScalingBehavior behavior) {
		this.scaleUpBehavior = behavior;
		return this;
	}

	//
	// Scaledown Behavior
	//
	@ApiModelProperty(required = true, value = "Scaledown Behavior")
	public ScalingBehavior getScaleDownBehavior() {
		return scaleDownBehavior;
	}

	public void setScaleDownBehavior(ScalingBehavior behavior) {
		this.scaleDownBehavior = behavior;
	}
	
	public CloudWatchAlarmCustomObjectSpec scaleDownBehavior(ScalingBehavior behavior) {
		this.scaleDownBehavior = behavior;
		return this;
	}

	//
	// Deployment
	//
	@ApiModelProperty(required = true, value = "Deployment")
	public String getDeployment() {
		return deployment;
	}

	public void setDeployment(String deployment) {
		this.deployment = deployment;
	}
	
	public CloudWatchAlarmCustomObjectSpec deployment(String deployment) {
		this.deployment = deployment;
		return this;
	}

	//
	// Alarm Configuration
	//
	@ApiModelProperty(required = true, value = "Alarm Configuration")
	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public CloudWatchAlarmCustomObjectSpec config(String config) {
		this.config = config;
		return this;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CloudWatchAlarmCustomObjectSpec that = (CloudWatchAlarmCustomObjectSpec) o;
		return Objects.equals(this.minReplicas, that.minReplicas)
			&& Objects.equals(this.maxReplicas, that.maxReplicas)
			&& Objects.equals(this.scaleUpBehavior, that.scaleUpBehavior)
			&& Objects.equals(this.scaleDownBehavior, that.scaleDownBehavior)
			&& Objects.equals(this.deployment, that.deployment)
			&& Objects.equals(this.config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(minReplicas, maxReplicas, config, deployment, scaleDownBehavior, scaleUpBehavior);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class V1CloudWatchAlarmCustomObjectSpec {\n");
		sb.append("    minReplicas: ").append(toIndentedString(minReplicas)).append("\n");
		sb.append("    maxReplicas: ").append(toIndentedString(maxReplicas)).append("\n");
		sb.append("    scaleUpBehavior: ").append(toIndentedString(scaleUpBehavior)).append("\n");
		sb.append("    scaleDownBehavior: ").append(toIndentedString(scaleDownBehavior)).append("\n");
		sb.append("    deployment: ").append(toIndentedString(deployment)).append("\n");
		sb.append("    config: ").append(toIndentedString(config)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
