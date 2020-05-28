package com.authine.cloudpivot.web.api.entity;


public class HBizAttachment {

  private String id;
  private String bizObjectId;
  private String bizPropertyCode;
  private java.sql.Timestamp createdTime;
  private String creater;
  private String fileExtension;
  private long fileSize;
  private String mimeType;
  private String name;
  private String parentBizObjectId;
  private String parentSchemaCode;
  private String refId;
  private String schemaCode;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getBizObjectId() {
    return bizObjectId;
  }

  public void setBizObjectId(String bizObjectId) {
    this.bizObjectId = bizObjectId;
  }


  public String getBizPropertyCode() {
    return bizPropertyCode;
  }

  public void setBizPropertyCode(String bizPropertyCode) {
    this.bizPropertyCode = bizPropertyCode;
  }


  public java.sql.Timestamp getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(java.sql.Timestamp createdTime) {
    this.createdTime = createdTime;
  }


  public String getCreater() {
    return creater;
  }

  public void setCreater(String creater) {
    this.creater = creater;
  }


  public String getFileExtension() {
    return fileExtension;
  }

  public void setFileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }


  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }


  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public String getParentBizObjectId() {
    return parentBizObjectId;
  }

  public void setParentBizObjectId(String parentBizObjectId) {
    this.parentBizObjectId = parentBizObjectId;
  }


  public String getParentSchemaCode() {
    return parentSchemaCode;
  }

  public void setParentSchemaCode(String parentSchemaCode) {
    this.parentSchemaCode = parentSchemaCode;
  }


  public String getRefId() {
    return refId;
  }

  public void setRefId(String refId) {
    this.refId = refId;
  }


  public String getSchemaCode() {
    return schemaCode;
  }

  public void setSchemaCode(String schemaCode) {
    this.schemaCode = schemaCode;
  }

}
