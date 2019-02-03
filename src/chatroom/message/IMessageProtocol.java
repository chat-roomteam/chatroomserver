package chatroom.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface IMessageProtocol {
	
	/**
	 * 消息类型
	 * @return
	 */
	String getType();

	/**
	 * 获取消息的Json字符串
	 * 
	 * @return 消息的Json字符串
	 */
	String toJsonString();

	/**
	 * 获取消息的json串字节序列数组
	 * 
	 * @return 消息的json串字节序列数组
	 * @throws UnsupportedEncodingException
	 */
	byte[] jsonBytes() throws UnsupportedEncodingException;

	/**
	 * 获取消息的json串字节序列数组长度
	 * 
	 * @return 消息的json串字节序列数组长度
	 * @throws IOException
	 */
	int jsonByteLen() throws UnsupportedEncodingException;

}