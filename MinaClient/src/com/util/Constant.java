package com.util;

import org.apache.mina.filter.codec.textline.LineDelimiter;

public class Constant {
	
	/**
	 * 流水号
	 * 规则：每次取得流水号需+1
	 */
	public static long serialNum = 0;
	public static long maxSerialNum = Long.parseLong("9999999999");
	
	/**
	 * 分割字符
	 */
	public static String one = "" + (char)1;	
	public static String three = "" + (char)3;	
	public static String four = "" + (char)4;	
	
	public static final LineDelimiter SPLIT_END = new LineDelimiter(""+(char)5);
	
	/**
	 * 通信事件名称
	 */
	public static String USER_ID = "EVT_ID";
	public static String ALIVE = "EVT_ALIVE";
	public static String RE_OK = "EVT_GET_RECORD_OK";
	public static String RECORD = "FUN_GET_RECORD";
	public static String RE_RET = "EVT_GET_RECORD_RET";
	
	/**
	 * MINA server信息
	 */
	public static String serverName = "127.0.0.1";
	//public static String serverName = "172.16.23.41";
	public static int serverPort = 9702;
	
	/**
	 * ftp服务器信息
	 */
//	public static String hostName = "172.16.23.28";
//	public static int port = 9703;
//	public static String userName = "admin";
//	public static String password = "admin";
	
	public static String headAddr = "F:\\fileRes\\";
	
	public static String hostName = "127.0.0.1";
	public static int port = 21;
	public static String userName = "admin";
	public static String password = "admin";
	
}
