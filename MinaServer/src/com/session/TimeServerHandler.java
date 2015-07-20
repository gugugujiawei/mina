package com.session;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.example.sumup.codec.Constants;

import com.object.ClientInfo;
import com.util.Constant;
import com.util.Log;

public class TimeServerHandler extends IoHandlerAdapter{

	public ClientInfo myClient = new ClientInfo("",0); 
	String TAG = "TimeServerHandler";
	
	@Override 
	public void sessionCreated(IoSession session) { 
		//显示客户端的ip和端口 
		System.out.println(session.getRemoteAddress().toString()); 
		// 设置IoSession一定间隔时间发送特定命令，参数单位是秒
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
	} 
	
	@Override 
	public void messageReceived( IoSession session, Object message ) throws Exception{
		
		String recMsg = message.toString(); 
		
		if( recMsg.trim().equalsIgnoreCase("quit") ) { 
			session.close();//结束会话 
			return; 
		}
		
		String []msgSplit = recMsg.split(Constant.one + "|" + Constant.three + "|" + Constant.four);
		
		int serialNum = Integer.parseInt(String.valueOf(msgSplit[0]));		
		Constant.seriaNum = serialNum;		
		String eventStr = msgSplit[1];
		
		if(eventStr.trim().equalsIgnoreCase(Constant.USER_ID)){
			myClient.setUserId(msgSplit[2]); 
			System.out.println("userID: " + msgSplit[2]);
		}else if(eventStr.trim().equalsIgnoreCase(Constant.ALIVE)){
			myClient.setAlive(true);
			System.out.println(msgSplit[0] + " client: "+ myClient.getUserId()+" is Alive.");		
		}else if(eventStr.trim().equalsIgnoreCase(Constant.RE_OK)){
			System.out.println( msgSplit[0] +" 前流水号" + msgSplit[2] + " fileSize: " + Integer.parseInt(String.valueOf(msgSplit[3]))/1024 + "KB");
		}else if(eventStr.trim().equalsIgnoreCase(Constant.RE_RET)){
			int result = Integer.parseInt(msgSplit[3]);
			myClient.setRecOK(result);
			Log.d(TAG,result + "");
			if(result == 0)
				System.out.println("success trans!");
			else
				System.out.println("fail trans!");
		}
		
		
//		char[] serialNum = new char[10];
//		//System.out.println(recMsg); 
//		recMsg.getChars(0, 10, serialNum, 0);		
//		myClient.setSerialNum(Integer.parseInt(String.valueOf(serialNum)));
//		System.out.println(String.valueOf(serialNum));
//		
//		Pattern pattern = Pattern.compile("(\\D+)3(\\S*)");
//		Matcher matcher = pattern.matcher(recMsg);
//		StringBuffer buffer = new StringBuffer();
//		
//		if(matcher.find()){ 
//			buffer.append(matcher.group(1)); 
//			String eventStr = matcher.group(1);
//			
//			String parmAll = matcher.group(2);
//			String[] parmList = parmAll.split("4");
//			//System.out.println(eventStr); 
//			String parm = "";
//			for(int i=0;i<parmList.length;i++){
//				//当多个参数时此处需要修改
//				parm = parmList[i];				
//			}
//			
//			//System.out.println(eventStr.trim().equalsIgnoreCase(Constant.ALIVE));
//			
//			if(eventStr.trim().equalsIgnoreCase(Constant.USER_ID)){
//				myClient.setUserId(parm); 
//				System.out.println("userID: " + parm);
//			}else if(eventStr.trim().equalsIgnoreCase(Constant.ALIVE)){
//				myClient.setAlive(true);
//				System.out.println("client: "+ myClient.getUserId()+" is Alive.");
//				//session.close();//结束会话 
//				//return; 
//			}else if(eventStr.trim().equalsIgnoreCase(Constant.RE_RET)){
//				myClient.setRecOK(Integer.parseInt(parm));
//				if(parm == "0")
//					System.out.println("success trans!");
//				else
//					System.out.println("fail trans!");
//			}
//		}else{
//			System.out.println("null"); 
//		}
				
//		if(myClient.getSerialNum() == 2){
//			System.out.println(myClient.getClientInfo()); 
//			session.write(myClient.getClientInfo());
//		}
		//String path = "F:\\file";		
		//session.write(path);
	}
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// TODO Auto-generated method stub
		super.sessionIdle(session, status);

		if (status == IdleStatus.BOTH_IDLE){
			if(Constant.seriaNum == 2 || Constant.seriaNum == 4 || Constant.seriaNum == 6 || Constant.seriaNum == 8 ||Constant.seriaNum == 10 || Constant.seriaNum == 12 ||Constant.seriaNum == 14 || Constant.seriaNum == 16 ||Constant.seriaNum == 18 || Constant.seriaNum == 20 ){
			myClient.setSerialNum(++Constant.seriaNum);
			System.out.println(myClient.getClientInfo()); 
			session.write(myClient.getClientInfo());
			}
		}
	}
	
}
