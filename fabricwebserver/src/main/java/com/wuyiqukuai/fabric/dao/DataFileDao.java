package com.wuyiqukuai.fabric.dao;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = "files")
public interface DataFileDao {
	
	@Cacheable
	String getFileJson(Integer page, Integer rows);

	byte[] getByteByTxId(String txId);

}
