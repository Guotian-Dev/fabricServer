package com.wuyiqukuai.fabric.dao.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hyperledger.fabric.sdkintegration.DataDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.wuyiqukuai.fabric.dao.DataHandleDao;
import com.wuyiqukuai.fabric.domain.ParamEntity;
import com.wuyiqukuai.fabric.util.DataHandleUtil;
import com.wuyiqukuai.fabric.util.DataJsonUtils;

@Repository
public class DataHandleDaoImpl implements DataHandleDao{
	
//	@Autowired
	private DataDriver dataDriver;
	
	private final static Logger logger = LoggerFactory.getLogger(DataHandleDaoImpl.class);
	
	@Override
	public void insertData(List<ParamEntity> paramEntityList) throws Exception{
		
		Iterator<ParamEntity> it = paramEntityList.iterator();
		String[] transData = null;
		
		String key = null;
		
		while(it.hasNext()) {
			ParamEntity paramEntity = it.next();
			transData = DataHandleUtil.paramHandle(DataHandleUtil.INSERTE_OPERATE, paramEntity.getKey(), paramEntity.getValue());
			key = paramEntity.getKey();
		}
		
		dataDriver.runTransaction(transData);
		
		logger.debug(key + "值为:" + dataDriver.queryByChainCode(key));
		
	}


	@Override
	public void batchInsertData(List<ParamEntity> paramEntityList) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String queryByKey(String key) {
		// TODO Auto-generated method stub
		return dataDriver.queryByChainCode(key);
		
	}

	@Override
	public void deleteByKey(String key) throws Exception {
		dataDriver.runTransaction(DataHandleUtil.paramHandle(DataHandleUtil.DELETE_OPERATE,key));
	}


	@Override
	public String queryBlockInfo(String peerName) throws Exception {
		return dataDriver.channelQuerry(peerName);
	}

	/**
	 * 具有peerName 返回的 payload和 txId的json串
	 */
	@Override
	public String queryByKeyAndPeerName(String key, String peerName) throws Exception {
		return dataDriver.queryByChainCode(key, peerName);
	}


	@Override
	public String queryBlock() throws Exception {
		return dataDriver.blockWalker();
	}


	@Override
	public String zz(String skf, String fkf, String je) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			dataDriver.runTransaction(DataHandleUtil.paramHandle(DataHandleUtil.MOVE_OPERATE, fkf, skf, je));
			
			String fkfData = dataDriver.queryByChainCode(fkf);
			String skfData = dataDriver.queryByChainCode(skf);
			
			map.put("执行状态", "执行成功");
			map.put("付款方余额", fkfData);
			map.put("收款方余额", skfData);
			
			return DataJsonUtils.toJson(map);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}


}
