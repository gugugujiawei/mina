package com.util;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.filter.codec.textline.TextLineEncoder;


/**
 * �ַ�����
 * @author �ַ����롢���빤���࣬������˹���
 *
 */
public class CharsetCodecFactory implements ProtocolCodecFactory {
	private ProtocolDecoder decoder;
	private ProtocolEncoder encoder;
	
	public CharsetCodecFactory(){
		TextLineDecoder textLineDecoder = new TextLineDecoder(Charset.forName("GBK"),Constant.SPLIT_END);
		textLineDecoder.setMaxLineLength(102400);
		decoder = textLineDecoder;
		TextLineEncoder textLineEncoder = new TextLineEncoder(Charset.forName("GBK"),Constant.SPLIT_END);
		textLineEncoder.setMaxLineLength(102400);
		encoder = textLineEncoder;
	}
	
	@Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }
 
    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
}
