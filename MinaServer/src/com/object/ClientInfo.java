package com.object;

import java.text.DecimalFormat;

import com.util.Constant;

public class ClientInfo {
	
	public ClientInfo(String userId, int serialNum){
		this.setUserId(userId);
		this.setSerialNum(serialNum);
		this.alive = false;
	}
	

	private String userId = "";
	private int serialNum = 0;
	private String eventStr = "";
	private String localAddr = "";
	private String remoteAddr = "";
	private int recOK = 0; 
	private boolean alive = false;
	
	public String getClientInfo(){
		
		DecimalFormat df=new DecimalFormat("0000000000");
	    String num=df.format(this.serialNum);	    
	    String path = "2015/06/24";
	    String fileName = "sx186.wav";
	    String catalog = "72030/2015/06/24";
	    //String catalog = "temp/123/234";
	    String msg = num + Constant.one + Constant.RECORD + Constant.three + "0026" + Constant.four + path + Constant.four + fileName + Constant.four + catalog;
		return msg;
	}
	
	public String getClientInfo2(){
		
		DecimalFormat df=new DecimalFormat("0000000000");
	    String num=df.format(this.serialNum);	    
	    String path = "F:\\fileRes\\";
	    String fileName = "commons.zip";
	    String catalog = "/2015/temp/";
		String msg = num + Constant.one + Constant.RECORD + Constant.three + "0026" + Constant.four + path + Constant.four + fileName + Constant.four + catalog;
		return msg;
	}
	
	public int getRecOK() {
		return recOK;
	}
	public void setRecOK(int recOK) {
		this.recOK = recOK;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public int getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}
	
	public String getEventStr() {
		return eventStr;
	}
	public void setEventStr(String eventStr) {
		this.eventStr = eventStr;
	}
	
	public String getLocalAddr() {
		return localAddr;
	}
	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}
	
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
