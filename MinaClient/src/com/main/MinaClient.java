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
		
		
		//�����ͻ���������. 
		//AbstractIoService\AbstractIoConnector
		connector = new NioSocketConnector(); 
		
		//connector.getFilterChain().addLast( "logger", new LoggingFilter() ); 

		/**
		 * ʹ���ַ�������
		 * ProtocolCodecFilter(new TextLineCodecFactory())
		 * ����������������ǽ��յ�����Ϣת����һ���е��ı��󴫵ݸ� IoHandler��������ǿ����� messageReceived ��ֱ�ӽ� msg ����ǿ��ת���� String ����
		 */
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CharsetCodecFactory())); //���ñ�������� 
		connector.setConnectTimeoutMillis(30000); //�������ӳ�ʱ 
		connector.setHandler(new TimeClientHandler());//�����¼������� 
		
		//���������ص�������  
        connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter(){  
        	
            @Override  
            public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {  
                for(;;){  
                    try{  
                        Thread.sleep(3000);  
                        ConnectFuture future = connector.connect(new InetSocketAddress(Constant.serverName, Constant.serverPort));  
                        future.awaitUninterruptibly();// �ȴ����Ӵ����ɹ�  
                        ioSession = future.getSession();// ��ȡ�Ự  
                        
                		//���͵�һ����userid����Ϣ
                        sendMsgWithID(ioSession);
                        
                        if(ioSession.isConnected()){  
                            Log.d("��������["+ connector.getDefaultRemoteAddress().getHostName() +":"+ connector.getDefaultRemoteAddress().getPort()+"]�ɹ�");  
                            break;  
                        }  
                    }catch(Exception ex){  
                        Log.d("������������¼ʧ��,3��������һ��:" + ex.getMessage());  
                    }  
                }  
            }  
        });
		
		ConnectFuture cf = connector.connect( 
		new InetSocketAddress(Constant.serverName, Constant.serverPort));//�������� 
		cf.awaitUninterruptibly();//�ȴ����Ӵ������ 		
		//���͵�һ����userid����Ϣ
		sendMsgWithID(cf.getSession());

		//cf.getSession().getCloseFuture().awaitUninterruptibly();//�ȴ����ӶϿ� 
		//connector.dispose(); 
    }
    
	/**
	 * ���ʹ����豸ID�ĺ���
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
