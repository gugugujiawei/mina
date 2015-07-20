package com.main;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.clientHandler.TimeClientHandler;
import com.object.SendInfo;
import com.object.FtpClientUtil;
import com.util.CharsetCodecFactory;
import com.util.Constant;
import com.util.Log;


public class MinaClient {

	public static String TAG = "MinaClient";
	
	public static NioSocketConnector connector;
	
    //public static IoSession ioSession;
    
    public MinaClient() {
        init();
    }
    
    public void init() {
		
		
		//创建客户端连接器. 
		//AbstractIoService\AbstractIoConnector
		connector = new NioSocketConnector(); 
		
		//connector.getFilterChain().addLast( "logger", new LoggingFilter() ); 

		/**
		 * 使用字符串编码
		 * ProtocolCodecFilter(new TextLineCodecFactory())
		 * 这个过滤器的作用是将收到的信息转换成一行行的文本后传递给 IoHandler，因此我们可以在 messageReceived 中直接将 msg 对象强制转换成 String 对象。
		 */
		connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new CharsetCodecFactory())); //设置编码过滤器 
		connector.setConnectTimeoutMillis(30000); //设置连接超时 
		connector.setHandler(new TimeClientHandler());//设置事件处理器 
		
		//断线重连回调拦截器  
        connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter(){  
        	
            @Override  
            public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {  
                for(;;){  
                    try{  
                        Thread.sleep(3000);  
                        ConnectFuture future = connector.connect(new InetSocketAddress(Constant.serverName, Constant.serverPort));  
                        future.awaitUninterruptibly();// 等待连接创建成功  
                        ioSession = future.getSession();// 获取会话  
                        
                		//发送第一个带userid的消息
                        sendMsgWithID(ioSession);
                        
                        if(ioSession.isConnected()){  
                            Log.d("断线重连["+ connector.getDefaultRemoteAddress().getHostName() +":"+ connector.getDefaultRemoteAddress().getPort()+"]成功");  
                            break;  
                        }  
                    }catch(Exception ex){  
                        Log.d("重连服务器登录失败,3秒再连接一次:" + ex.getMessage());  
                    }  
                }  
            }  
        });
		
		ConnectFuture cf = connector.connect( 
		new InetSocketAddress(Constant.serverName, Constant.serverPort));//建立连接 
		cf.awaitUninterruptibly();//等待连接创建完成 		
		//发送第一个带userid的消息
		sendMsgWithID(cf.getSession());

		//cf.getSession().getCloseFuture().awaitUninterruptibly();//等待连接断开 
		//connector.dispose(); 
    }
    
	/**
	 * 发送带有设备ID的函数
	 * @param ioSession
	 */
    public void sendMsgWithID(IoSession ioSession){
		SendInfo sendObj = new SendInfo(++Constant.serialNum,Constant.USER_ID,"72030");
		String sendMsgStr = sendObj.getSendInfo();
		ioSession.write(sendMsgStr);
    }
    
	public static void main(String[] args)  throws Exception { 
		
		MinaClient client = new MinaClient();

	}

}
