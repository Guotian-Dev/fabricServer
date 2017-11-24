package com.wuyiqukuai.fabric.service;

import java.util.Map;

public interface ChainCodeService {
	
	public Map<String, Object> enterHandle(String datas);

	public String queryByKey(String key);

	public void deleteByKey(String key);

	public String getBlockInfo(String peerName);

	public String queryByKeyAndPeerName(String key, String peerName);

	public String getBlock();

	public String zz(String skf, String fkf, String je);
}
