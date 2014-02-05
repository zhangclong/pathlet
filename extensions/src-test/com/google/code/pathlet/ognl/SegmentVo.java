package com.google.code.pathlet.ognl;


public class SegmentVo extends BaseAuditVo {
	private static final long serialVersionUID = 451896339250572185L;

	private Long segmentId;
	private String name;
	private String code;
	private String criteriaScheme;
	private String configVersion;
	private String sortName;
	private String sortOrder;
	private Long maxCount = 0L;
	private Long calCount = 0L;// 初始客群数量为0，用于标示计算的状态：  -1为计算中
	private java.sql.Timestamp calCountTime;
	private String allowModifier;

	//added by fuby 20130728 start
	private String ownerLevel; //创建者所属级别:GROUP:院线,REGION:区域;CINEMA:影城
	private String ownerRegion;//创建者所属区域:区域代码,dim104
	private Long ownerCinema;  //创建者所属影城:影城seqid
	//end of added by fuby

	public String getOwnerLevel() {
		return ownerLevel;
	}

	public void setOwnerLevel(String ownerLevel) {
		this.ownerLevel = ownerLevel;
	}

	public String getOwnerRegion() {
		return ownerRegion;
	}

	public void setOwnerRegion(String ownerRegion) {
		this.ownerRegion = ownerRegion;
	}

	public Long getOwnerCinema() {
		return ownerCinema;
	}

	public void setOwnerCinema(Long ownerCinema) {
		this.ownerCinema = ownerCinema;
	}

	public Long getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(Long segmentId) {
		this.segmentId = segmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCriteriaScheme() {
		return criteriaScheme;
	}

	public void setCriteriaScheme(String criteriaScheme) {
		this.criteriaScheme = criteriaScheme;
	}

	public String getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(String configVersion) {
		this.configVersion = configVersion;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Long getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Long maxCount) {
		this.maxCount = maxCount;
	}

	public Long getCalCount() {
		return calCount;
	}

	public void setCalCount(Long calCount) {
		this.calCount = calCount;
	}

	public java.sql.Timestamp getCalCountTime() {
		return calCountTime;
	}

	public void setCalCountTime(java.sql.Timestamp calCountTime) {
		this.calCountTime = calCountTime;
	}

	public String getAllowModifier() {
		return allowModifier;
	}

	public void setAllowModifier(String allowModifier) {
		this.allowModifier = allowModifier;
	}

	public Long retrieveSeqId() {
		return getSegmentId();
	}
	
	public String retrieveCode() {
		return getCode();
	}

}
