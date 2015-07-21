package com.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream; 
import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import com.clientHandler.TimeClientHandler;
import com.object.SendInfo;
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
    
    public static void initConfig(){
    	
    	Properties properties = new Properties();
    	String filePath = System.getProperty("user.dir") + "/conf/init.properties";        	 
    	try {
    		InputStream in = new BufferedInputStream(new FileInputStream(filePath)); 
			properties.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Constant.UID = (String)properties.getProperty("UID");
    	
    	Constant.serverName = (String) properties.get("serverName");
    	Constant.serverPort = Integer.parseInt((String)properties.get("serverPort"));
    	
    	Constant.ftpHostName = (String)properties.getProperty("ftpHostName");
    	Constant.ftpPort = Integer.parseInt((String)properties.getProperty("ftpPort"));
    	Constant.userName = (String)properties.getProperty("userName");
    	Constant.password = (String)properties.getProperty("password");
    	
    	Constant.threadChannel = Integer.parseInt((String)properties.getProperty("threadChannel"));
    	Constant.headAddr = (String)properties.getProperty("headAddr");
    	
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
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CharsetCodecFactory())); //设置编码过滤器 
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
		SendInfo sendObj = new SendInfo(++Constant.serialNum,Constant.USER_ID,Constant.UID);
		String sendMsgStr = sendObj.getSendInfo();
		ioSession.write(sendMsgStr);
    }
    
	public static void main(String[] args)  throws Exception { 
		
		initConfig();
		MinaClient client = new MinaClient();

	}

}
