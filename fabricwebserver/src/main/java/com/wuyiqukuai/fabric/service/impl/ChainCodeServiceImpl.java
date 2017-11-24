package com.wuyiqukuai.fabric.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wuyiqukuai.fabric.dao.DataHandleDao;
import com.wuyiqukuai.fabric.domain.ParamEntity;
import com.wuyiqukuai.fabric.service.ChainCodeService;
import com.wuyiqukuai.fabric.util.DataJsonUtils;

@Service
public class ChainCodeServiceImpl implements ChainCodeService{
	
	@Autowired
	private DataHandleDao dataHandleDao;

	@Override
	public Map<String, Object> enterHandle(String datas){
		
		//参数处理
		//数据处理 String[] "insert", "d", "9999"
		
		List<ParamEntity> paramEntityList = DataJsonUtils.jsonToList(datas, ParamEntity.class);
//		System.out.println(paramEntityList);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		int size = paramEntityList.size();
		
		if(size != 0) {
			if(size > 1) {
				dataHandleDao.batchInsertData(paramEntityList);
			}
			try {
				dataHandleDao.insertData(paramEntityList);
				map.put("success", "录入成功");
			} catch (Exception e) {
				map.put("failed", "录入失败");
			}
		}
		
		return map;
	}

	@Override
	public String queryByKey(String key) {
		return dataHandleDao.queryByKey(key);
	}

	@Override
	public void deleteByKey(String key) {
		try {
			dataHandleDao.deleteByKey(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBlockInfo(String peerName) {
		try {
			return dataHandleDao.queryBlockInfo(peerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String queryByKeyAndPeerName(String key, String peerName) {
		try {
			return dataHandleDao.queryByKeyAndPeerName(key, peerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getBlock() {
		try {
			return dataHandleDao.queryBlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String zz(String skf, String fkf, String je) {
		return dataHandleDao.zz(skf, fkf, je);
	}
}
