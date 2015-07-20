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
		
		
		//�����ͻ���������. 
		//AbstractIoService\AbstractIoConnector
		connector = new NioSocketConnector(); 
		
		//connector.getFilterChain().addLast( "logger", new LoggingFilter() ); 

		/**
		 * ʹ���ַ�������
		 * ProtocolCodecFilter(new TextLineCodecFactory())
		 * ����������������ǽ��յ�����Ϣת����һ���е��ı��󴫵ݸ� IoHandler��������ǿ����� messageReceived ��ֱ�ӽ� msg ����ǿ��ת���� String ����
		 */
		connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new CharsetCodecFactory())); //���ñ�������� 
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
		SendInfo sendObj = new SendInfo(++Constant.serialNum,Constant.USER_ID,"72030");
		String sendMsgStr = sendObj.getSendInfo();
		ioSession.write(sendMsgStr);
    }
    
	public static void main(String[] args)  throws Exception { 
		
		MinaClient client = new MinaClient();

	}

}
