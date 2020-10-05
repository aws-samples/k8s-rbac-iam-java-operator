package com.amazonwebservices.blogs.containers.kubernetes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.yaml.snakeyaml.Yaml;

import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroup;
import com.amazonwebservices.blogs.containers.kubernetes.model.IamUserGroupCustomObject;

import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapBuilder;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.spring.extended.controller.annotation.AddWatchEventFilter;
import io.kubernetes.client.spring.extended.controller.annotation.DeleteWatchEventFilter;
import io.kubernetes.client.spring.extended.controller.annotation.KubernetesReconciler;
import io.kubernetes.client.spring.extended.controller.annotation.KubernetesReconcilerReadyFunc;
import io.kubernetes.client.spring.extended.controller.annotation.KubernetesReconcilerWatch;
import io.kubernetes.client.spring.extended.controller.annotation.KubernetesReconcilerWatches;
import io.kubernetes.client.spring.extended.controller.annotation.UpdateWatchEventFilter;
import io.kubernetes.client.util.generic.GenericKubernetesApi;

//
// Reconciler beans that are annotated with @KubernetesReconciler annotation are processed by KubernetesReconcilerProcessor 
// which is an implementation of Spring’s BeanFactoryPostProcessor interface.
// This bean post-processor handles the task of creating a Controller for each Reconciler. 
// The Controller will be named based on the value attribute of @KubernetesReconciler annotation.
// Additionally, Under the hoods, it creates ControllerWatch instances for each of the KubernetesReconcilerWatch annotations on the Reconciler bean 
// so that the Controller is set up to handle add/update/delete event notifications pertaining to all the Kubernetes esources identified by those annotations
//
@KubernetesReconciler(
		value = "iamUserGroupController", 
		workerCount = 2,
		watches = @KubernetesReconcilerWatches({
			@KubernetesReconcilerWatch(
					workQueueKeyFunc = WorkQueueKeyFunFactory.IamGroupCustomObjectWorkQueueKeyFunc.class,
					apiTypeClass = IamUserGroupCustomObject.class, 
					resyncPeriodMillis = 60*1000L)
			}))
public class IamUserGroupReconciler implements Reconciler {
	private static final String CUSTOM_RESOURCE_NAMESPACE = "kube-system";
	private static final String CONFIGMAP_NAME = "aws-auth";
	private static final String MAPUSERS_KEY = "mapUsers";
	private static final String EMPTY_MAP = "[]";
	private static final String USERNAME_FIELD = "username";
	private static final String GROUPS_FIELD = "groups";
	
	
	private static final Logger logger = LogManager.getLogger(IamUserGroupReconciler.class);
	
	private GenericKubernetesApi<V1ConfigMap, V1ConfigMapList> apiConfigMap;
	private SharedInformer<IamUserGroupCustomObject> iamUserGroupInformer;
	private Map<String,IamUserGroupCustomObject> deletedObjects = new HashMap<String,IamUserGroupCustomObject>();
	private Map<String,IamUserGroupCustomObject> addedObjects = new HashMap<String,IamUserGroupCustomObject>();
	private Map<String,IamUserGroupCustomObject> updatedObjects = new HashMap<String,IamUserGroupCustomObject>();
	
	public IamUserGroupReconciler(ApiClient apiClient, SharedInformer<IamUserGroupCustomObject> iamGroupInformer) {
		this.iamUserGroupInformer = iamGroupInformer;
		
		this.apiConfigMap = new GenericKubernetesApi<V1ConfigMap, V1ConfigMapList>(
				V1ConfigMap.class, 
				V1ConfigMapList.class,
				"", 
				"v1", 
				"configmaps", 
				apiClient);
	}

	@KubernetesReconcilerReadyFunc
	public boolean informerReady() {
		return iamUserGroupInformer.hasSynced();
	}

	@AddWatchEventFilter(apiTypeClass = IamUserGroupCustomObject.class)
	public boolean onAddFilter(IamUserGroupCustomObject iamUserGroup) {
		if (!iamUserGroup.getMetadata().getNamespace().equals(CUSTOM_RESOURCE_NAMESPACE)) return false;
		String name = iamUserGroup.getMetadata().getName();
		String namespace = iamUserGroup.getMetadata().getNamespace();
		addedObjects.put(name.concat(namespace), iamUserGroup);
		logger.info(String.format("Handling onAdd event for IamUserGroup custom resource %s.%s", name, namespace));
		return true;
	}

	@UpdateWatchEventFilter(apiTypeClass = IamUserGroupCustomObject.class)
	public boolean onUpdateFilter(IamUserGroupCustomObject oldIamUserGroup, IamUserGroupCustomObject newIamUserGroup) {
		if (!newIamUserGroup.getMetadata().getNamespace().equals(CUSTOM_RESOURCE_NAMESPACE)) return false;
		if (oldIamUserGroup.equals(newIamUserGroup)) return false;
		String name = newIamUserGroup.getMetadata().getName();
		String namespace = newIamUserGroup.getMetadata().getNamespace();
		updatedObjects.put(name.concat(namespace), newIamUserGroup);
		logger.info(String.format("Handling onUpdate event for IamUserGroup custom resource %s.%s", name, namespace));
		return true;
	}

	@DeleteWatchEventFilter(apiTypeClass = IamUserGroupCustomObject.class)
	public boolean onDeleteFilter(IamUserGroupCustomObject iamUserGroup, boolean deletedFinalStateUnknown) {
		if (!iamUserGroup.getMetadata().getNamespace().equals(CUSTOM_RESOURCE_NAMESPACE)) return false;
		String name = iamUserGroup.getMetadata().getName();
		String namespace = iamUserGroup.getMetadata().getNamespace();
		deletedObjects.put(name.concat(namespace), iamUserGroup);
		logger.info(String.format("Handling onDelete event for IamUserGroup custom resource %s.%s", name, namespace));
		return true;
	}

	@Override
	public Result reconcile(Request request) {
		logger.info(String.format("Triggered reconciliation for %s.%s", request.getName(), request.getNamespace()));
		modifyConfigMap (request.getName(), request.getNamespace());
		return new Result(false);
	}
	
	@SuppressWarnings("unchecked")
	private void modifyConfigMap (String name, String namespace) {
		try {
			boolean isAdded = false;
			boolean isUpdated = false;
			boolean isDeleted = false;
			IamUserGroupCustomObject iamUserGroup = null;
			String objKey = name.concat(namespace);
			if (addedObjects.containsKey(objKey)) {
				isAdded = true;
				iamUserGroup = addedObjects.get(objKey);
				addedObjects.remove(objKey);
			}
			else if (updatedObjects.containsKey(objKey)) {
				isUpdated = true;
				iamUserGroup = updatedObjects.get(objKey);
				updatedObjects.remove(objKey);
			}
			else if (deletedObjects.containsKey(objKey)) {
				isDeleted = true;
				iamUserGroup = deletedObjects.get(objKey);
				deletedObjects.remove(objKey);
			}
		
			V1ConfigMapList configMapList = apiConfigMap.list(namespace).getObject();
			for (V1ConfigMap configMap : configMapList.getItems()) {
				String configMapName = configMap.getMetadata().getName();
				if (configMapName.equalsIgnoreCase(CONFIGMAP_NAME)) {
					logger.info(String.format("Making changes to ConfigMap %s.%s to reflect IAM changes", configMapName, namespace));
					
					//
					// Retrieve the list of mappings under the "mapUsers" key of the "aws-auth" config map
					// Note that even though the data section is provided as YAML in the K8s manifest, it is treated as a text blob
					// Hence, we will have to use SnakeYaml to process this YAML string and extract a Map<String, Object> representation from it.
					//
					Map<String,Object> dataYml = parseYaml(configMap.getData());
					List<Map<String,Object>> mapUsersList = (List<Map<String,Object>>) dataYml.get(MAPUSERS_KEY);
					if (mapUsersList == null) mapUsersList = new ArrayList<Map<String,Object>>();
					
					if (isAdded || isUpdated) {
						//
						// When a create event is triggered through IAM notification, the IamGroup custom object will contain only one group association for a user
						// Hence, if there already is a mapping for the given user in the "aws-auth" config map, we will have to add the group(s) in the new IamGroup object to the existing group associations
						//
						String userToAdd = iamUserGroup.getSpec().getUsername();
						Map<String, Object> mapToModify = null;
						for (Map<String, Object> map : mapUsersList) {
							if (map.get(USERNAME_FIELD).equals(userToAdd)) {
								mapToModify = map;
								break;
							}
						}
						if (mapToModify != null) {
							List<String> groups = (List<String>) mapToModify.get(GROUPS_FIELD);
							groups.addAll(iamUserGroup.getSpec().getIamGroups());
						} else {
							IamUserGroup iamUser = new IamUserGroup();
							iamUser.setUsername(iamUserGroup.getSpec().getUsername());
							iamUser.setUserarn(iamUserGroup.getSpec().getIamUser());
							iamUser.setGroups(iamUserGroup.getSpec().getIamGroups());
							mapUsersList.add(iamUser.toMap());
							dataYml.put(MAPUSERS_KEY, mapUsersList);
						}
					}
					else if (isDeleted) {
						//
						// When a delete event is triggered through IAM notification, the IamGroup custom object will contain only one group association for a user.
						// OTOH, when a delete event is triggered through "kubectl delete -f", it can contain multiple groups associations for a user.
						// Hence, if there already is a mapping for the given user in the "aws-auth" config map, we will have to remove only the group(s) in the deleted IamGroup custom object from the existing group associations
						//
						String userToDelete = iamUserGroup.getSpec().getUsername();
						Map<String,Object> mapToDelete = null;
						for (Map<String, Object> map : mapUsersList) {
							if (map.get(USERNAME_FIELD).equals(userToDelete)) {
								mapToDelete = map;
								break;
							}
						}
						if (mapToDelete != null) {
							List<String> groups = (List<String>) mapToDelete.get(GROUPS_FIELD);
							groups.removeAll(iamUserGroup.getSpec().getIamGroups());
							if (groups.isEmpty()) mapUsersList.remove(mapToDelete);
						}
					}
					
					Yaml y = new Yaml ();
					Map<String,String> newDataYaml = new HashMap<String,String>();
					for (String key : dataYml.keySet()) {
						newDataYaml.put(key, y.dump(dataYml.get(key)));
					}
				
					V1ConfigMap newConfigMap = new V1ConfigMapBuilder()
							.withNewMetadata()
							.withName(configMapName)
							.withNamespace(namespace)
							.endMetadata()
							.addToData(newDataYaml)
							.build();
					
					newConfigMap = apiConfigMap.update(newConfigMap).getObject();
					logger.info(String.format("Updated the ConfigMap %s.%s with resource version %s", 
							configMapName, 
							namespace,
							newConfigMap.getMetadata().getResourceVersion()));
				}
			}
		} catch (IOException e) {
			logger.error(String.format("IOException occured when updarting ConfigMap '%s.%s'", name, namespace));
		} catch (Exception e) {
			logger.error(String.format("Exception occured when updating ConfigMap '%s.%s'; %s", name, namespace, e.getMessage()), e);
		}
	}
	
	
	private Map<String,Object> parseYaml (Map<String, String> mapData) throws IOException {
		StringBuilder sBuilder = new StringBuilder();
		Set<String> keys = mapData.keySet();
		for (String k : keys) {
			if (mapData.get(k).startsWith(EMPTY_MAP)) continue;
			sBuilder.append(String.format("%s:", k));
			sBuilder.append(System.lineSeparator());
			sBuilder.append(mapData.get(k));
		}
		Yaml y = new Yaml();
		Map<String,Object> yaml = y.load(sBuilder.toString());
		return yaml;
	}
}

