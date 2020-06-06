package com.octank.kubernetes.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel(description = "IamUserGroupCustomObjectSpec describes how a user wants their resource to appear")
public class IamUserGroupCustomObjectSpec {
	public static final String SERIALIZED_IAM_USER = "iamUser";
	@SerializedName(SERIALIZED_IAM_USER)
	private String iamUser;
	
	public static final String SERIALIZED_IAM_GROUPS = "iamGroups";
	@SerializedName(SERIALIZED_IAM_GROUPS)
	private List<String> iamGroups = new ArrayList<String>();

	public static final String SERIALIZED_USERNAME = "username";
	@SerializedName(SERIALIZED_USERNAME)
	private String username;

	//
	// iamUser
	//
	@ApiModelProperty(required = true, value = "IAM user")
	public String getIamUser() {
		return iamUser;
	}

	public void setIamUser(String iamUser) {
		this.iamUser = String.format("%s", iamUser);;
	}
	
	public IamUserGroupCustomObjectSpec iamUser(String iamUser) {
		this.iamUser = String.format("%s", iamUser);
		return this;
	}
	
	//
	// iamGroup
	//
	@ApiModelProperty(required = true, value = "IAM groups")
	public List<String> getIamGroups() {
		return iamGroups;
	}

	public void setIamGroup(String iamGroup) {
		this.iamGroups.add(iamGroup);
	}
	
	public void setIamGroups(List<String> iamGroups) {
		this.iamGroups = iamGroups;
	}

	public IamUserGroupCustomObjectSpec group(String iamGroup) {
		this.iamGroups.add(iamGroup);
		return this;
	}
	
	public IamUserGroupCustomObjectSpec groups(List<String> iamGroups) {
		this.iamGroups = iamGroups;
		return this;
	}

	//
	// username
	//
	@ApiModelProperty(required = true, value = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public IamUserGroupCustomObjectSpec username(String username) {
		this.username = username;
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
		IamUserGroupCustomObjectSpec that = (IamUserGroupCustomObjectSpec) o;
		return Objects.equals(this.iamUser, that.iamUser)
				&& Objects.equals(this.iamGroups, that.iamGroups)
				&& Objects.equals(this.username, that.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(iamGroups, iamUser, username);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class V1IamUserGroupCustomObjectSpec {\n");
		sb.append("    iamUser: ").append(toIndentedString(iamUser)).append("\n");
		sb.append("    iamGroups: ").append(toIndentedString(iamGroups)).append("\n");
		sb.append("    user: ").append(toIndentedString(username)).append("\n");
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
