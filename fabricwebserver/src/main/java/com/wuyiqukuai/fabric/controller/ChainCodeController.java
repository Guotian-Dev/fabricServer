package com.wuyiqukuai.fabric.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wuyiqukuai.fabric.domain.PeerDomain;
import com.wuyiqukuai.fabric.service.ChainCodeService;
import com.wuyiqukuai.fabric.util.DataJsonUtils;

@RequestMapping("/chaincode")
@Controller
public class ChainCodeController {
	
	private final static Logger logger = LoggerFactory.getLogger(ChainCodeController.class);
	
	@Autowired
	private ChainCodeService chainCodeService;
	
	@RequestMapping("/brow")
	public String toBrow() {
		return "brow";
	}
	@RequestMapping("/blockBrow")
	public String toBlockBrow(Map<String, Object> map) {
		//将block信息放到map中
		String blockInfo = chainCodeService.getBlockInfo("peer0.org1.example.com");
		logger.debug(blockInfo);
		Map<String, String> blockMap = DataJsonUtils.jsonToMap(blockInfo, String.class);
		
		map.put("block", blockMap);
		
		return "blockBrow";
	}
	
	@RequestMapping("/peerInfo")
	@ResponseBody
	public String getPeerInfo() {
		//将block信息放到map中
		String blockInfo = chainCodeService.getBlockInfo("peer0.org1.example.com");
		logger.debug(blockInfo);
		
		return blockInfo;
	}
	
	@RequestMapping("/blockInfo")
	@ResponseBody
	public String getBlockInfo() {
		//获取链上所有块的信息
		return chainCodeService.getBlock();
		
	}
	
	
	@RequestMapping("/dataQuery")
	public String toDataQuery() {
		return "blockDataQuery";
	}
	
	@RequestMapping("/dataMove")
	public String toDataMove() {
		
		return "dataMove";
	}
	
	@RequestMapping("/zz")
	@ResponseBody
	public String zz(String skf, String fkf, String je) {
		
		logger.debug("skf:" + skf);
		logger.debug("fkf:" + fkf);
		logger.debug("je:" + je);
		
		String rtData = chainCodeService.zz(skf, fkf, je);
		
		if(rtData == null) {
			return String.valueOf("执行失败");
		}
		return rtData;
	}
	
	@RequestMapping("/add")
	public String toAdd() {
		return "enterData";
	}
	
	@RequestMapping("/peerJson")
	@ResponseBody
	public String getPeerJson() {
		//peer值
		PeerDomain peer1 = new PeerDomain("peer0.org1.example.com", "grpc://10.0.6.226:7051");
		PeerDomain peer2 = new PeerDomain("peer1.org1.example.com", "grpc://10.0.6.226:7056");
		PeerDomain peer3 = new PeerDomain("peer0.org2.example.com", "grpc://10.0.6.226:8051");
		PeerDomain peer4 = new PeerDomain("peer1.org2.example.com", "grpc://10.0.6.226:8056");
		
		List<PeerDomain> listPeer = new ArrayList<PeerDomain>();
		listPeer.add(peer1);
		listPeer.add(peer2);
		listPeer.add(peer3);
		listPeer.add(peer4);
		
		String json = DataJsonUtils.toJson(listPeer);
		return json;
	}
	
	@RequestMapping("/query")
	public String toQuery() {
		return "query";
	}
	
	@RequestMapping("/delete")
	public String toDelete() {
		return "delete";
	}
	
	@RequestMapping("/deleteByKey")
	@ResponseBody
	public String doDelete(String key) {
		logger.debug((key));
		chainCodeService.deleteByKey(key);
		return String.valueOf(true);
	}
	
	@RequestMapping("/queryByKey")
	@ResponseBody
	public String doQuery(String key, String peerName) {
		
//		logger.debug(key);
//		String value = chainCodeService.queryByKey(key);
//		logger.debug(value);
//		return String.valueOf(value);
		
		logger.debug(key);
		logger.debug(peerName);
		
		//peerName 设置为固定值
		
		if(peerName == null || "".equals(peerName)) {
			String value = chainCodeService.queryByKey(key);
			return String.valueOf(value);
		} else {
			//{"payload":"900","txId":"156db1f77e760c297b69663d7f332fefbdc912dfd3014f5a27209636a320c9ed"}
			String info = chainCodeService.queryByKeyAndPeerName(key, "peer0.org1.example.com");
			logger.debug(info);
			return info;
		}
	}
	
	@RequestMapping("/addDataToChain")
	@ResponseBody
	public Map<String, Object> addDataToChain(String datas) {
		
		logger.debug(datas);
		
//		ExecutorService pool = Executors.newSingleThreadExecutor();
//		
//		pool.submit(new Runnable() {
//			@Override
//			public void run() {
//				chainCodeService.enterHandle(datas);
//			}
//		});
		
		chainCodeService.enterHandle(datas);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", "录入成功");
		return map;
	}
	
}
