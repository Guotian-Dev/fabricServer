package com.wuyiqukuai.fabric.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wuyiqukuai.fabric.domain.PeerDomain;
import com.wuyiqukuai.fabric.domain.UserInfo;
import com.wuyiqukuai.fabric.service.DataFileService;
import com.wuyiqukuai.fabric.util.DataJsonUtils;

@RequestMapping("/file")
@Controller
public class DataFileController {
	
	private final static Logger logger = LoggerFactory.getLogger(DataFileController.class);
	
	@Autowired
	private DataFileService dataFileService;
	
	@RequestMapping("/handle")
	public String toDataHandle(HttpServletRequest request) {
		
		//获取session
		HttpSession session = request.getSession();
		UserInfo userInfo = (UserInfo) session.getAttribute("user");
		
		if(userInfo == null) {
			return "login";
		}
		
		return "fileHandle";
	}
	
	@RequestMapping("/fileList")
	@ResponseBody
	public String getFileList(String startTime, String endTime, Integer page, Integer rows, String peerName) {
		logger.debug("fileList");
		logger.debug("startTime->" + startTime);
		logger.debug("endTime->" + endTime);
		logger.debug("page->" + page);
		logger.debug("rows->" + rows);
		logger.debug("peerName->" + peerName);
		
		logger.debug("------------获取文件列表---------");
		
		//获取块上文件的数据（Map） total 该页file信息
		Map<String, Object> fileJsonMap = dataFileService.getFilesByPage(startTime, endTime, page, rows);
		
		
		String rtJson = DataJsonUtils.toJson(fileJsonMap);
		
		logger.debug("rtJson->" + rtJson);
		
		return rtJson;
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/download")
	public ResponseEntity<String> downFile(Integer page, Integer rows, String fileName, String upload_time, String fileSize, String block_size, String block_total, String id, String file_uuid, HttpServletResponse response, HttpServletRequest request) {
		
		logger.info("------------执行下载------------");
		
		logger.debug("page:" + page);
		logger.debug("rows:" + rows);
		logger.debug("fileName:" + fileName);
		logger.debug("fileSize:" + fileSize);
		logger.debug("block_size:" + block_size);
		logger.debug("block_total:" + block_total);
		logger.debug("id:" + id);
		logger.debug("upload_time" + upload_time);
		logger.debug("file_uuid" + file_uuid);
		
		int fSize = Integer.valueOf(fileSize);
		
		
		ServletOutputStream out = null;
		try {
			
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/x-download");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-Length", fileSize);
			response.setHeader("Content-Disposition",
					"attachment;fileName=" + java.net.URLEncoder.encode(fileName , "UTF-8"));

			long pos = 0;
			
			if (null != request.getHeader("Range")) {
				// 断点续传
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				try {
					pos = Long.parseLong(request.getHeader("Range").replaceAll("bytes=", "").replaceAll("-", ""));
				} catch (NumberFormatException e) {
					pos = 0;
				}
			}
			
			logger.debug("pos->" + pos);
			
			out = response.getOutputStream();
			
			//设置响应的contentR 
			String contentRange = new StringBuffer("bytes ").append(pos + "").append("-").append((fSize - 1) + "")
					.append("/").append(fSize + "").toString();

			response.setHeader("Content-Range", contentRange);
			
			//通过txIds获取bytes
			List<String> txIds = dataFileService.getTxidsByFileName(fileName,file_uuid, page, rows);
			
			String json = DataJsonUtils.toJson(txIds);
			
			logger.debug("json:" + json);
			
			
			Iterator<String> iterator = txIds.iterator();
			
			while(iterator.hasNext()) {
				String txId = iterator.next();
				logger.debug(txId);
				
				byte[] b = dataFileService.getBytesByTxId(txId);
				
				if(b != null) {
					out.write(b, 0, b.length);
				}
				
			}
			
		} catch (Exception e) {
		} finally {
			try {
				if (null != out)
					out.flush();
				if (null != out)
					out.close();
			} catch (IOException e) {
			}
		}
		return new ResponseEntity(null, HttpStatus.OK);
	}
}
