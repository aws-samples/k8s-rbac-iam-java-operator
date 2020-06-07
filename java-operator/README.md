# Java-based Custom Controller

The Kubernetes operator is implemented using <a href="https://github.com/kubernetes-client/java">Kubernetes Java SDK</a>. This operator packages a custom resource named IamUserGroup defined by a CustomResourceDefinition, a custom controller implemented as a Deployment, which responds to events in the Kubernetes cluster pertaining to add/update/delete actions on the IamUserGroup custom resource, Role/RoleBinding resources that allow the custom controller to make changes to the aws-auth ConfigMap.

# Build Requirements

<ul>
  <li><a href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">Java SE Development Kit 8</a></li>
  <li><a href="https://maven.apache.org/download.cgi">Apache Maven 3.6.2</a></li>
  <li><a href="https://www.docker.com/products/container-runtime">Docker 19.03</a></li>
</ul>
