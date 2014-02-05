package com.google.code.newpath.jdbc.example.vo;

import java.util.Date;


public class FileInfoVO {

	//File unique ID, generated by system in creation time.
	private long fileId;
	
	//File name, 
	private String fileName;
	
	//File size
	private long fileSize;
	
	/** Content Type string comply with HTTP  protocol content type */
	private String contentType;

	/** Create time stamp. */
	private Date creationTime;

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}


}
