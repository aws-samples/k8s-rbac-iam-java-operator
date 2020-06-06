package com.octank.kubernetes.model;

import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.openapi.models.V1ListMeta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel(description = "IamUserGroupCustomObjectList is a list of IamGroupCustomObject objects.")
public class IamUserGroupCustomObjectList {
  public static final String SERIALIZED_NAME_API_VERSION = "apiVersion";
  @SerializedName(SERIALIZED_NAME_API_VERSION)
  private String apiVersion;

  public static final String SERIALIZED_NAME_ITEMS = "items";
  @SerializedName(SERIALIZED_NAME_ITEMS)
  private List<IamUserGroupCustomObject> items = new ArrayList<IamUserGroupCustomObject>();

  public static final String SERIALIZED_NAME_KIND = "kind";
  @SerializedName(SERIALIZED_NAME_KIND)
  private String kind;

  public static final String SERIALIZED_NAME_METADATA = "metadata";
  @SerializedName(SERIALIZED_NAME_METADATA)
  private V1ListMeta metadata;

  public IamUserGroupCustomObjectList apiVersion(String apiVersion) {
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

  public IamUserGroupCustomObjectList items(List<IamUserGroupCustomObject> items) {

    this.items = items;
    return this;
  }

  public IamUserGroupCustomObjectList addItemsItem(IamUserGroupCustomObject itemsItem) {
    this.items.add(itemsItem);
    return this;
  }

  @ApiModelProperty(
      required = true,
      value = "items list individual CustomResourceDefinition objects")
  public List<IamUserGroupCustomObject> getItems() {
    return items;
  }

  public void setItems(List<IamUserGroupCustomObject> items) {
    this.items = items;
  }

  public IamUserGroupCustomObjectList kind(String kind) {

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

  public IamUserGroupCustomObjectList metadata(V1ListMeta metadata) {
    this.metadata = metadata;
    return this;
  }

  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  public V1ListMeta getMetadata() {
    return metadata;
  }

  public void setMetadata(V1ListMeta metadata) {
    this.metadata = metadata;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IamUserGroupCustomObjectList that = (IamUserGroupCustomObjectList) o;
    return Objects.equals(this.apiVersion, that.apiVersion)
        && Objects.equals(this.items, that.items)
        && Objects.equals(this.kind, that.kind)
        && Objects.equals(this.metadata, that.metadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiVersion, items, kind, metadata);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1IamUserGroupCustomObjectList {\n");
    sb.append("    apiVersion: ").append(toIndentedString(apiVersion)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
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
