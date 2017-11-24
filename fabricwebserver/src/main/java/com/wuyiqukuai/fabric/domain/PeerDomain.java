package com.wuyiqukuai.fabric.domain;

public class PeerDomain {
	
	private String peerName;
	private String peerIp;
	
	
	public PeerDomain() {
		super();
	}
	public PeerDomain(String peerName, String peerIp) {
		super();
		this.peerName = peerName;
		this.peerIp = peerIp;
	}
	public String getPeerName() {
		return peerName;
	}
	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}
	public String getPeerIp() {
		return peerIp;
	}
	public void setPeerIp(String peerIp) {
		this.peerIp = peerIp;
	}

}
