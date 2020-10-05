package com.amazonwebservices.blogs.containers.kubernetes.model;

import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@ApiModel(description = "IamUserGroupCustomObject represents a resource that should be exposed on the API server")
public class IamUserGroupCustomObject implements io.kubernetes.client.common.KubernetesObject {
  public static final String SERIALIZED_NAME_API_VERSION = "apiVersion";
  @SerializedName(SERIALIZED_NAME_API_VERSION)
  private String apiVersion;

  public static final String SERIALIZED_NAME_KIND = "kind";
  @SerializedName(SERIALIZED_NAME_KIND)
  private String kind;

  public static final String SERIALIZED_NAME_METADATA = "metadata";
  @SerializedName(SERIALIZED_NAME_METADATA)
  private V1ObjectMeta metadata;

  public static final String SERIALIZED_NAME_SPEC = "spec";
  @SerializedName(SERIALIZED_NAME_SPEC)
  private IamUserGroupCustomObjectSpec spec;

  public IamUserGroupCustomObject apiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
    return this;
  }

  @javax.annotation.Nullable
  @ApiModelProperty(
      value = 
          "APIVersion defines the versioned schema of this representation of an object. Servers should convert recognized schemas to the latest internal value, and may reject unrecognized values. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#resources")
  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public IamUserGroupCustomObject kind(String kind) {
    this.kind = kind;
    return this;
  }

  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Kind is a string value representing the REST resource this object represents. Servers may infer this from the endpoint the client submits requests to. Cannot be updated. In CamelCase. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#types-kinds")
  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public IamUserGroupCustomObject metadata(V1ObjectMeta metadata) {
    this.metadata = metadata;
    return this;
  }

  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  public V1ObjectMeta getMetadata() {
    return metadata;
  }

  public void setMetadata(V1ObjectMeta metadata) {
    this.metadata = metadata;
  }

  public IamUserGroupCustomObject spec(IamUserGroupCustomObjectSpec spec) {
    this.spec = spec;
    return this;
  }

  @ApiModelProperty(required = true, value = "")
  public IamUserGroupCustomObjectSpec getSpec() {
    return spec;
  }

  public void setSpec(IamUserGroupCustomObjectSpec spec) {
    this.spec = spec;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IamUserGroupCustomObject that = (IamUserGroupCustomObject) o;
    return Objects.equals(this.apiVersion, that.apiVersion)
        && Objects.equals(this.kind, that.kind)
        && Objects.equals(this.metadata, that.metadata)
        && Objects.equals(this.spec, that.spec);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiVersion, kind, metadata, spec);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1IamUserGroupCustomObject {\n");
    sb.append("    apiVersion: ").append(toIndentedString(apiVersion)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    spec: ").append(toIndentedString(spec)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
