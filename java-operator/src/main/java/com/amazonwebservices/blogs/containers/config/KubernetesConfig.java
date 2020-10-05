package com.amazonwebservices.blogs.containers.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonwebservices.blogs.containers.kubernetes.ControllerRunner;
import com.amazonwebservices.blogs.containers.kubernetes.IamUserGroupReconciler;
import com.amazonwebservices.blogs.containers.kubernetes.SpringSharedInformerFactory;
import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObject;

import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.CustomClientBuilder;

@Configuration
public class KubernetesConfig {

	@Bean
	public ApiClient apiClient() throws IOException {
		ApiClient apiClient = CustomClientBuilder.custom();
		//ApiClient apiClient = ClientBuilder.cluster().build();
		return apiClient.setHttpClient(apiClient.getHttpClient().newBuilder().readTimeout(Duration.ZERO).build());
	}

	@Bean
    public SharedInformerFactory sharedInformerFactory(ApiClient apiClient) {
      return new SpringSharedInformerFactory(apiClient);
    }

	@Bean
	public IamUserGroupReconciler iamUserGroupReconciler(ApiClient apiClient, SharedInformer<IamUserGroupCustomObject> iamGroupInformer) {
		return new IamUserGroupReconciler(apiClient, iamGroupInformer);
	}
	
	@Bean
	public ControllerRunner controllerRunner (
			SharedInformerFactory sharedInformerFactory,
			@Qualifier("iamUserGroupController") Controller iamUserGroupController) {
		return new ControllerRunner(sharedInformerFactory, iamUserGroupController);
	}
}
