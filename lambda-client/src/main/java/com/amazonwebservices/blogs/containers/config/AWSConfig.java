package com.amazonwebservices.blogs.containers.config;

public class AWSConfig {
	public static final String STS_ENDPOINT = "STS_ENDPOINT";
	public static final String AWS_REGION = "REGION";
	public static final String ASSUMED_ROLE = "ASSUMED_ROLE";
	public static final String ACCESS_KEY_ID = "ACCESS_KEY_ID";
	public static final String SECRET_ACCESS_KEY = "SECRET_ACCESS_KEY";

	public static String getSTSEndpoint() {
		return System.getenv(STS_ENDPOINT);
	}

	public static String getRegion() {
		return System.getenv(AWS_REGION);
	}

	public static String getAccessKey() {
		return System.getenv(ACCESS_KEY_ID);
	}

	public static String getSecretKey() {
		return System.getenv(SECRET_ACCESS_KEY);
	}

	public static String getAssumedRole() {
		return System.getenv(ASSUMED_ROLE);
	}
}
