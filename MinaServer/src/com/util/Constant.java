package com.util;

import org.apache.mina.filter.codec.textline.LineDelimiter;

public class Constant {
	
	/**
	 * 流水号
	 * 规则：每次取得流水号需+1
	 */
	public static int seriaNum = 0;
	
	public static String USER_ID = "EVT_ID";
	public static String ALIVE = "EVT_ALIVE";
	public static String RE_OK = "EVT_GET_RECORD_OK";
	public static String RECORD = "FUN_GET_RECORD";
	public static String RE_RET = "EVT_GET_RECORD_RET";
	public static final LineDelimiter SPLIT_END = new LineDelimiter(""+(char)5);
	/**
	 * 分割字符
	 */
	public static String one = "" + (char)1;	
	public static String three = "" + (char)3;	
	public static String four = "" + (char)4;	
}
