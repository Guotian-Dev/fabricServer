//package com.wuyiqukuai.fabric.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import com.wuyiqukuai.fabric.domain.Lot;
//import com.wuyiqukuai.fabric.service.LotService;
//
//@RequestMapping("/lot")
//@Controller
//public class LotController {
//	
//	@Autowired
//	private LotService lotService;
//	
//	@RequestMapping("/add")
//	public String genLot(String redNumber, String blueNumber, Integer id) {
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		
//		map.put("redNumber", redNumber);
//		map.put("blueNumber", blueNumber);
//		map.put("id", id);
//		
//		if(id != null) {
//			lotService.modifyLotById(map);
//			return "redirect:/lot/list";
//		}
//		
//		lotService.addLot(map);
//		
//		return "redirect:/lot/list";
//		
//	}
//	
//	@RequestMapping("/jxAdd")
//	public String jxLot() {
//		lotService.jxAddLot();
//		return "redirect:/lot/list";
//	}
//	
//	
//	@RequestMapping("/list")
//	public String lotList(Map<String, Object> map, Integer pageNum, Integer pageSize) {
//		
//		pageNum = pageNum == null ? 1 : pageNum;  
//		pageSize = pageSize == null ? 10 : pageSize; 
//		
//		PageHelper.startPage(pageNum, pageSize);
//		List<Lot> lotList = lotService.getLots();
//		
//		PageInfo<Lot> pageInfo = new PageInfo<Lot>(lotList);
//		
//		//在页面lotList.list 可以拿到包装好的数据
//		map.put("lotList", pageInfo);
//		
//		return "lotList";
//		
//	}
//	
//	@RequestMapping("/modify")
//	public String lotModify(Integer id, Map<String, Object> map) {
//		
//		Lot lot = lotService.getLotById(id);
//		map.put("lot", lot);
//		
//		return "modify";
//		
//	}
//	
//	@RequestMapping("/delete")
//	public String lotDelete(Integer id) {
//		
//		lotService.deleteLotById(id);
//		return "redirect:/lot/list";
//		
//	}
//
//}
