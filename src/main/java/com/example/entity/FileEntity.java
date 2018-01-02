package com.example.entity;

import java.io.Serializable;

/**
 * 文件属性实体类，根据自己的实际情况添加
 * @author admin
 *
 */
public class FileEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	//文件名称
	private String fileName;
	//文件大小
	private long fileSize;
	//文件描述
	private String describe;
	//文件上传路径
	private String filepath;
	//文件类型
	private String fileType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public void setFileSize(long fineSize) {
		this.fileSize = fineSize;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
