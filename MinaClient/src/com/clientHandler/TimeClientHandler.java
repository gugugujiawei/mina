package com.clientHandler;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import com.object.RecInfo;
import com.object.SendInfo;
import com.object.FtpClientUtil;
import com.util.Constant;
import com.util.Log;

/**
 * 默认短连接，长连接阻塞必须定义一个thread pool否则无法处理更多的连接数
 * @author Gogary @Email gugugujiawei@gmail.com
 * @time 2015-7-19 下午10:13:47
 */

public class TimeClientHandler extends IoHandlerAdapter{ 
	
	private final String TAG = "TimeClientHandler";
	
	@Override 
	public void messageReceived(IoSession session, Object message) throws Exception { 	
		
		Log.d(TAG,message.toString());		
		
		//解析服务器发来的消息
		RecInfo recObj = new RecInfo(message);		
		
		//FtpClientUtil ftputil = new FtpClientUtil("172.16.23.28", 9703, "admin", "admin", false);
		//ftputil.setFileSave("F:\\fileRes\\", "minaTest.txt", "/2015/temp/");
		
		//FtpClientUtil ftputil = FtpClientUtil.getInstance();
		//Log.d(TAG,(recObj.getEventStr().equalsIgnoreCase(Constant.RECORD))+"");
		if(recObj.getEventStr().equalsIgnoreCase(Constant.RECORD)){
			FtpClientUtil ftputil = new FtpClientUtil(false);
			//注册回调函数
			Callback mCallback = new ClientCallback(session,recObj.getSerialNum());
			ftputil.registCallback(mCallback);
			
			
			HashMap<String,String> queElement = new HashMap<String,String>();
			queElement.put("1",recObj.getLocalAddr());
			queElement.put("2",recObj.getFileName());
			queElement.put("3",recObj.getRemoteAddr());
			ftputil.setQueElement(queElement);
	
			
			/**
			 * 计算文件大小并返回
			 */
			File f = new File(recObj.getLocalAddr() + recObj.getFileName());
			FileInputStream fis = new FileInputStream(f);
			int byteSize = fis.available();
			
			DecimalFormat df=new DecimalFormat("0000000000");
		    String num=df.format(recObj.getSerialNum());		
			SendInfo sendByteSize = new SendInfo(++Constant.serialNum,Constant.RE_OK,num,byteSize+"");
			String sendMsg = sendByteSize.getSendInfo();		
			session.write(sendMsg);
		}

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionCreated(session);
		// 设置IoSession一定间隔时间发送特定命令，参数单位是秒
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 9);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// TODO Auto-generated method stub
		super.sessionIdle(session, status);
		// 定时发送ALIVE信号
		if (status == IdleStatus.BOTH_IDLE){
			SendInfo obj1 = new SendInfo(++Constant.serialNum,Constant.ALIVE,"");
			String sendMsg = obj1.getSendInfo();		
			session.write(sendMsg);
		}
	}	
		
}

class ClientCallback implements Callback{
	private IoSession session = null;
	private long serialNum = 0; 
	
	public ClientCallback(IoSession session,long serialNum){
		this.session = session;
		this.serialNum = serialNum;
	}
	@Override
	public void callback(boolean result) {
		// TODO Auto-generated method stub		
		int resultNum = (result == true ? 0:-1);
		Log.d("resultNum","\""+resultNum+"\"");
		DecimalFormat df=new DecimalFormat("0000000000");
	    String num=df.format(serialNum);	    
		SendInfo sendByteSize = new SendInfo(++Constant.serialNum,Constant.RE_RET,num,resultNum+"");
		String sendMsg = sendByteSize.getSendInfo();		
		session.write(sendMsg);
		//传输完文件后ftp服务器关闭ftp连接
		//FtpClientUtil.getInstance().close();
	}
}


