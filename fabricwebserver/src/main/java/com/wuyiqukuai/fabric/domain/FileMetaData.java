package com.wuyiqukuai.fabric.domain;

import java.util.List;

//{upload_time=2017-08-16 14:45:36, 
//tx_ids=[778cf1fedb7e611c6b98f7da587bc243e6be7ea8af0729730a0223722f6457be],
//file_name=res.zip, file_id=0e74768ae9d4fab314492a8ed51882072aca2459, 
//block_number=1.0, block_size=1048576.0, file_size=195196.0}

/**
 * 文件元数据
 * @author PC
 *
 */
public class FileMetaData {
	
	private String upload_time;
	private String file_name;
	private int block_number;
	private long block_size;
	private long file_size;
	private String file_id;
	private String file_uuid;
	
	private List<String> tx_ids;
	
	public String getFile_uuid() {
		return file_uuid;
	}
	public void setFile_uuid(String file_uuid) {
		this.file_uuid = file_uuid;
	}
	public List<String> getTx_ids() {
		return tx_ids;
	}
	public void setTx_ids(List<String> tx_ids) {
		this.tx_ids = tx_ids;
	}
	public String getUpload_time() {
		return upload_time;
	}
	public void setUpload_time(String upload_time) {
		this.upload_time = upload_time;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	
	public int getBlock_number() {
		return block_number;
	}
	public void setBlock_number(int block_number) {
		this.block_number = block_number;
	}
	public long getBlock_size() {
		return block_size;
	}
	public void setBlock_size(int block_size) {
		this.block_size = block_size;
	}
	public long getFile_size() {
		return file_size;
	}
	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}
	public String getFile_id() {
		return file_id;
	}
	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}
	@Override
	public String toString() {
		return "FileMetaData [upload_time=" + upload_time + ", file_name=" + file_name + ", block_number="
				+ block_number + ", block_size=" + block_size + ", file_size=" + file_size + ", file_id=" + file_id
				+ ", tx_ids=" + tx_ids + "]";
	}
	
}
