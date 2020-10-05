package com.octank.kubernetes.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModelProperty;

public class ScalingPolicy implements Comparable<ScalingPolicy> {
	public enum ScalingType {Pods, Percent};
	
	public static final String SERIALIZED_VALUE = "value";
	@SerializedName(SERIALIZED_VALUE)
	private int value;
	
	public static final String SERIALIZED_TYPE = "type";
	@SerializedName(SERIALIZED_TYPE)
	private ScalingType type;
	
	//
	// Scaling value
	//
	@ApiModelProperty(required = true, value = "Scaling value")
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public ScalingPolicy value(int value) {
		this.value = value;
		return this;
	}

	
	//
	// Scaling type
	//
	@ApiModelProperty(required = true, value = "Scaling type")
	public ScalingType getType() {
		return type;
	}
	
	public void setType(ScalingType type) {
		this.type = type;
	}
	
	public ScalingPolicy type(ScalingType type) {
		this.type = type;
		return this;
	}

	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		ScalingPolicy that = (ScalingPolicy) o;
		return Objects.equals(this.value, that.value)
			&& Objects.equals(this.type, that.type);
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}
	
	
	@Override
	public int compareTo(ScalingPolicy that) {
		if (this.equals(that))
			return 0;

		if (Objects.equals(this.type.ordinal(), that.type.ordinal())) {
			if (this.value >= that.value)
				return 1;
			else
				return -1;
		}
		else if (this.type.ordinal() >= that.type.ordinal())
			return 1;
		else
			return -1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ScalingPolicy {\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
