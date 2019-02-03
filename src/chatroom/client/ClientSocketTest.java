package chatroom.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import org.apache.log4j.Logger;

import chatroom.message.IMessageProtocol;
import chatroom.message.TalkMessage;
import chatroom.model.Talk;

public class ClientSocketTest {

	public static Logger logger = Logger.getLogger(ClientSocketTest.class);

	public static void main(String args[]) throws Exception {

		logger.info("start ClientSocket....");

		// 要连接的服务端IP地址和端口
		String host = "127.0.0.1";
		int port = 55533;
		// 与服务端建立连接
		Socket socket = new Socket(host, port);

		// 建立连接后获得输出流
		OutputStream outputStream = socket.getOutputStream();
		
		for(int i=0;i<10;i++) {
			TalkMessage talkMessage = new TalkMessage();
			
			
			talkMessage.setId(UUID.randomUUID().toString());
			
			Talk t=new Talk();
			 
			t.setTimestamp(System.currentTimeMillis());
			
			t.setContent("hello,"+i);
			
			talkMessage.setTalk(t);

			sendMessage(outputStream, talkMessage);
		}

		outputStream.close();
		socket.close();
	}
	
	
	/**
	 * 发送消息
	 * 
	 * @param outputStream
	 * @param msg
	 * @throws IOException
	 */
	public static void sendMessage(OutputStream outputStream, IMessageProtocol msg) throws IOException {
		
		outputStream.write(msg.jsonByteLen() >> 8);
		outputStream.write(msg.jsonByteLen());
		outputStream.write(msg.jsonBytes());
		outputStream.flush();
	
	}

}
