package com.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.session.TimeServerHandler;
import com.util.CharsetCodecFactory;


public class MinaServer {
	private static final int PORT = 9702;//定义监听端口 
	public static void main( String[] args ) throws IOException 
	{ 
		IoAcceptor acceptor = new NioSocketAcceptor(); 
		//acceptor.getFilterChain().addLast( "logger", new LoggingFilter() ); 
		acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new CharsetCodecFactory()));//指定编码过滤器 
		acceptor.getFilterChain().addLast("threadPool",new ExecutorFilter(Executors.newCachedThreadPool()));
		acceptor.setHandler( new TimeServerHandler() );//指定业务逻辑处理器 
		acceptor.setDefaultLocalAddress( new InetSocketAddress(PORT) );//设置端口号 
		acceptor.bind();//启动监听 
	} 
}
