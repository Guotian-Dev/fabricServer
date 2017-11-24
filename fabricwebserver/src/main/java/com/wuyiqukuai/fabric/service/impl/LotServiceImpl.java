//package com.wuyiqukuai.fabric.service.impl;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.wuyiqukuai.fabric.dao.LotDao;
//import com.wuyiqukuai.fabric.domain.Lot;
//import com.wuyiqukuai.fabric.service.LotService;
//
//@Service
//public class LotServiceImpl implements LotService{
//	
//	@Autowired
//	private LotDao lotDao;
//
//	@Override
//	public List<Lot> getLots() {
//		return lotDao.selectLots();
//	}
//
//	@Override
//	public void addLot(Map<String, Object> map) {
//		lotDao.insertLot(map);
//	}
//
//	@Override
//	public Lot getLotById(Integer id) {
//		return lotDao.selectLotById(id);
//	}
//
//	@Override
//	public void modifyLotById(Map<String, Object> map) {
//		lotDao.updateLotById(map);
//	}
//
//	@Override
//	public void deleteLotById(Integer id) {
//		lotDao.updateLotState(id);
//	}
//
//	@Override
//	public void jxAddLot() {
//		//投注号码由6个红色球号码和1个蓝色球号码组成。红色球号码从01--33中选择；蓝色球号码从01--16中选择。
//		String redNumber = randomRedNumber();
//		String blueNumber = randomBlueNumber();
//		
//		Map<String, Object> map = new HashMap<String,Object>();
//		map.put("redNumber", redNumber);
//		map.put("blueNumber", blueNumber);
//		
//		addLot(map);
//	}
//	
//	/**
//	 * 生成红号
//	 * @return
//	 */
//	public String randomRedNumber() {
//		
//		StringBuilder redNumber = new StringBuilder();
//		
//		for (int i = 0; i < 6; i++) {
//			redNumber.append(makeNum((int)(33*Math.random()+1),2));
//		}
//		return redNumber.toString();
//	}
//	
//	/**
//	 * 生成蓝号
//	 * @return
//	 */
//	public String randomBlueNumber() {
//		return makeNum((int)(16*Math.random()+1),2);
//	}
//	
//	/**
//	 * 填充 0
//	 * @param num
//	 * @param length
//	 * @return
//	 */
//	private static String makeNum(int num, int length) {
//		return String.format("%0"+length+"d", num);
//	}
//	
//}
