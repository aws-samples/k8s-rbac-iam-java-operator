package com.amazonwebservices.blogs.containers.kubernetes;

import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObject;
import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObjectList;

import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.spring.extended.controller.annotation.GroupVersionResource;
import io.kubernetes.client.spring.extended.controller.annotation.KubernetesInformer;
import io.kubernetes.client.spring.extended.controller.annotation.KubernetesInformers;

//
// An instance of KubernetesInformerFactoryProcessor which implements Spring’s BeanDefinitionRegistryPostProcessor is used to process the beans annotated with KubernetesInformers annotation.
// It will instantiate a SharedIndexInformer (which extends SharedInformer) for each KubernetesInformer and register that as a Spring Bean
// A SharedIndexInformer exposes the getIndexer method which retrieves a list of Kubernetes resource objects represented by the Indexer class.
// The KubernetesInformerFactoryProcessor wraps this instance inside a Lister and registers that as a Spring Bean
// Thus, if we have a KubernetesInformer annotation whose apiTypeClass attribute is set to V1Pod and apiListTypeClass is set to ViPodList,
// then configuring a KubernetesInformerFactoryProcessor will automatically gives as Spring Beans for the classes SharedIndexInformer<V1Pod> and Lister<V1Pod>. 
//
@KubernetesInformers({
	@KubernetesInformer(
			apiTypeClass = IamUserGroupCustomObject.class, 
			apiListTypeClass = IamUserGroupCustomObjectList.class, 
			groupVersionResource = @GroupVersionResource(
					apiGroup = "octank.com", 
					apiVersion = "v1", 
					resourcePlural = "iamusergroups"), 
			resyncPeriodMillis = 60	* 1000L)
	})
public class SpringSharedInformerFactory extends SharedInformerFactory {
	public SpringSharedInformerFactory (ApiClient apiClient) {
		super (apiClient);
	}
}