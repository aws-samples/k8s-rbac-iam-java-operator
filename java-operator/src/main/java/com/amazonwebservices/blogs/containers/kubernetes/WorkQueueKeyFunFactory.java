package com.amazonwebservices.blogs.containers.kubernetes;

import java.util.function.Function;

import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObject;

import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;

public interface WorkQueueKeyFunFactory {
	class V1PodWorkQueueKeyFunc implements Function<V1Pod, Request> {
		@Override
		public Request apply(V1Pod obj) {
			V1ObjectMeta objectMeta = obj.getMetadata();
			return new Request(objectMeta.getNamespace(), objectMeta.getName());
		}
	}
	
	class IamGroupCustomObjectWorkQueueKeyFunc implements Function<IamUserGroupCustomObject, Request> {
		@Override
		public Request apply(IamUserGroupCustomObject obj) {
			V1ObjectMeta objectMeta = obj.getMetadata();
			return new Request(objectMeta.getNamespace(), objectMeta.getName());
		}
	}
}
