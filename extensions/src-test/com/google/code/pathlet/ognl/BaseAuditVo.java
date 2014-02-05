package com.google.code.pathlet.ognl;

import java.io.Serializable;

/**
 * 
 * 提供基本的创建人、修改人、修改时间、修改人、乐观锁版本字段。
 * 
 * @author Charlie Zhang
 *
 */
public abstract class BaseAuditVo implements Serializable {

	private static final long serialVersionUID = 8443462499784925070L;
	
	private String createBy;
	private java.sql.Timestamp createDate;
	private String updateBy;
	private java.sql.Timestamp updateDate;
	private Long version;
	
	public abstract String retrieveCode();
	
	public abstract Long retrieveSeqId();
	
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public java.sql.Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public java.sql.Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(java.sql.Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
}
