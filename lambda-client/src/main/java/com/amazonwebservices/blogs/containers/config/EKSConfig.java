package com.amazonwebservices.blogs.containers.config;

public class EKSConfig {
	public static final String API_SERVER = "API_SERVER";
	public static final String CLUSTER_NAME = "CLUSTER_NAME";
	public static final String CERT_AUTHORITY = "CERT_AUTHORITY";
	private static final String EKS_TOKEN_PREFIX = "k8s-aws-v1.";

	public static String getApiServer() {
		return System.getenv(API_SERVER);
	}

	public static String getClusterName() {
		return System.getenv(CLUSTER_NAME);
	}

	public static String getCertAuthority() {
		return System.getenv(CERT_AUTHORITY);
	}

	public static String getEKSTokenPrefix() {
		return EKS_TOKEN_PREFIX;
	}
}
