package com.wuyiqukuai.fabric.domain;

/**
 * 彩票-双色球
 * @author PC
 *
 */
public class Lot {
	
	private Integer id;
	//库里面以String保存，每个号之间由，分割 01-33
	private String redNumber;
	//01-16
	private String blueNumber;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRedNumber() {
		return redNumber;
	}
	public void setRedNumber(String redNumber) {
		this.redNumber = redNumber;
	}
	public String getBlueNumber() {
		return blueNumber;
	}
	public void setBlueNumber(String blueNumber) {
		this.blueNumber = blueNumber;
	}
}
