package com.object;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

import com.clientHandler.Callback;
import com.util.Constant;
import com.util.Log;

public class FtpClientUtil{

	public static String TAG = "FtpClientUtil";
	 
	public FTPClient ftpClient = null;  
	      
	//ftp��������ַ  
	private String hostName;  
	      
	//ftp������Ĭ�϶˿�  
	public int port = 21;  
	          
	//��¼��  
	private String userName;  
	          
	//��¼����     
	private String password;  
	          
	//�ļ���
	private Callback mCallback = null;
	
	//��ͨ���У��������Է������Ĵ����ļ���Ϣ
	public static Queue<Map> queue = new LinkedList<Map>();	
	
	//�̶߳��У�ÿ��ֻ����5�����룬���BlockQueueû�пռ�,����ô˷������̱߳����
	public static BlockingQueue<Runnable> threadQueue = new ArrayBlockingQueue<Runnable>(Constant.threadChannel); 
	
	//�̳߳أ�5���̣߳��ŵ���
	/**
     * ThreadPoolExecutor
     * corePoolSize�� �̳߳�ά���̵߳���������
     * maximumPoolSize���̳߳�ά���̵߳��������
     * keepAliveTime�� �̳߳�ά���߳�������Ŀ���ʱ��
     * unit�� �̳߳�ά���߳�������Ŀ���ʱ��ĵ�λ
     * workQueue�� �̳߳���ʹ�õĻ������
     * handler�� �̳߳ضԾܾ�����Ĵ������
     */
	public static ThreadPoolExecutor fixedThreadPool = new ThreadPoolExecutor(Constant.threadChannel, Constant.threadChannel, 1, TimeUnit.HOURS, threadQueue, new ThreadPoolExecutor.CallerRunsPolicy());;
	
	
	//�̶߳���Runnable
    Runnable run = new Runnable() {  
    	
        @SuppressWarnings("unchecked")
		@Override  
	    public void run() {  		
        	Map<String,String> queElement;
			synchronized(queue){
				queElement = queue.element();
				queue.remove();
			}
			String localDir = queElement.get("1");
			String fileName = queElement.get("2");
			String remoteDir = queElement.get("3");
			
			if(!isDirExist(remoteDir)){
				Log.d("no exit","heheh");
				try {
					mkDirs(remoteDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Log.d("exit","heheh");
			}
			
			BufferedInputStream buffIn = null;  
		    try{  
		    	Log.d(TAG,"Runable()");
		        buffIn = new BufferedInputStream(new FileInputStream(localDir + fileName));  
		        boolean result = ftpClient.storeFile(remoteDir + fileName, buffIn); 
		        mCallback.callback(result);
		        close();		        
		    }catch(Exception e){  
		        e.printStackTrace();  
		        close();
		    }finally{
		        try{  
		            if(buffIn!=null)  
		                buffIn.close();  
		        }catch(Exception e){  
		        	e.printStackTrace();  
		        	close();
		        }  
		    }		    		    				        		       
       } 
    };
	
	/** 
	 * hostName    ������ַ 
	 * port        �˿ں� 
	 * userName    �û��� 
	 * password    ���� 
	 * @param is_zhTimeZone    �Ƿ�������FTP Server�� 
	 * @return 
	 */ 
	
	public FtpClientUtil(boolean is_zhTimeZone){		
		this.hostName = Constant.ftpHostName;  
		this.port = Constant.ftpPort;
	    this.userName = Constant.userName;  
	    this.password = Constant.password;       
	    	          	   
        
	    //this.fixedThreadPool = Executors.newFixedThreadPool(threadNum);
	    
	    this.ftpClient = new FTPClient();  
	    ftpClient.setConnectTimeout(60000);       //���ӳ�ʱΪ60��	
	    
	    //����Ҫ���ı���ʱ
        if(is_zhTimeZone){  
            this.ftpClient.configure(FtpClientUtil.Config());  
            this.ftpClient.setControlEncoding("GBK");  
        }  
               
        this.login();        
        this.setFileType(FTPClient.BINARY_FILE_TYPE);     
        
	}

	
//	private static class FtpClientUtilHolder {   
//        static FtpClientUtil instance = new FtpClientUtil(false);   
//    }   
//  
//  
//    public static FtpClientUtil getInstance() {   
//        return FtpClientUtilHolder.instance;   
//    } 
	 

    /**
     * ע��ص�����ftpClient�� 
     * @param callback
     */
	public void registCallback(Callback callback){
		mCallback = callback;
	}
    
    /** 
     * ��¼FTP������ 
     */  
    private void login(){
    	boolean isLogined = false;
        try{          	
            ftpClient.connect(this.hostName,this.port);
            int i = 0;
            while(!isLogined && i<3){          
            	isLogined = ftpClient.login(this.userName, this.password);
            	i++;
            }
        } catch (FTPConnectionClosedException e){  
            // TODO Auto-generated catch block 
        	Log.d(TAG,"FTP����ʧ��");
            e.printStackTrace();  
        } catch (IOException e){  
            // TODO Auto-generated catch block
        	Log.d(TAG,"IO����FTP����������ʧ��");
            e.printStackTrace();  
        }  
        if(isLogined){
        	System.out.println("�ɹ����Ӳ���½��ftp������:" + this.hostName);
        }else{
        	//�û������������
        	Log.d("3�ε�¼ʧ��");
        }
    }  
    
      
    private static FTPClientConfig Config(){  
        FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);  
        conf.setRecentDateFormatStr("MM��dd�� HH:mm");  
        conf.setServerLanguageCode("zh");
        //conf.setRecentDateFormatStr("(YYYY��)?MM��dd��( HH:mm)?");  
        return conf;  
    }
    
    /** 
     * ���ô����ļ�������[�ı��ļ����߶������ļ�] 	
     * @param fileType--BINARY_FILE_TYPE,ASCII_FILE_TYPE  
     */  
	public void setFileType(int fileType){  
		try{  
	        ftpClient.setFileType(fileType);  
	    }catch(IOException e){  
	        e.printStackTrace();  
        }  
    }  
	
	/**
	 * �����ļ�����Դ·�����ļ��������ֲ��䣩��FTPĿ��·��,��hashmap��װ
	 * @param queElement
	 */
	
	public void setQueElement(HashMap<String,String> queElement){
				
		queue.add(queElement);		
		//ÿ��add�󶼻����upload�ȴ�
		uploadFile();
	}
	
	      
	/**  
     * �����ȴ����Ӷ�����ȡ�����ϴ��ļ���FTP������ 
	 */  
	public void uploadFile(){
		
		if(!ftpClient.isConnected()){
			login();
		}		
		fixedThreadPool.execute(run);		
				
	} 
	
	
	/**
	 * ���Ŀ¼�Ƿ����
	 * @param dir
	 * @return
	 */
	public boolean isDirExist(String dir) {
		try {
			return ftpClient.changeWorkingDirectory(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			return false;
		}		
	}
	
	/**
	 * �𼶴���Ŀ¼
	 * @param path
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void mkDirs(String path) throws IllegalStateException,IOException{
		if (null == path) {
			return;
		}

		ftpClient.changeWorkingDirectory("/");// �л�����Ŀ¼
		StringTokenizer dirs = new StringTokenizer(path, "/");
		String temp = null;
		while (dirs.hasMoreElements()) {
			temp = dirs.nextElement().toString();
			if (!isDirExist(temp)) {
				ftpClient.makeDirectory(temp);// ����Ŀ¼
				ftpClient.changeWorkingDirectory(temp);// ���봴����Ŀ¼
			}
		}
	}
	
	/** 
     * �ر�FTP���� 
     */  
    public void close(){  
    	try{  
    		Log.d(TAG,"ftp close()");
	        if(ftpClient!=null){  
	            ftpClient.logout();  
	            ftpClient.disconnect();  
	        }  
	    }catch (Exception e){  
	        e.printStackTrace();  
	    }  
    } 
}