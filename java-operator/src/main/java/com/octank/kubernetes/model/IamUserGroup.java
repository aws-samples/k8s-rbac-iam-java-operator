package com.octank.kubernetes.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IamUserGroup {
	private String username;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	private List<String> groups;
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	private String userarn;
	public String getUserarn() {
		return userarn;
	}
	public void setUserarn(String userarn) {
		this.userarn = userarn;
	}
	
	public Map<String,Object> toMap (){
		Map<String,Object> objMap = new HashMap<String,Object>();
		objMap.put("userarn", userarn);
		objMap.put("username", username);
		objMap.put("groups", groups);
		return objMap;
	}
}
