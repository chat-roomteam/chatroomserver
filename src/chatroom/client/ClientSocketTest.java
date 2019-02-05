package chatroom.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import chatroom.message.IMessageProtocol;
import chatroom.message.LoginMessage;
import chatroom.message.MessageType;
import chatroom.message.TalkMessage;
import chatroom.model.User;

public class ClientSocketTest {

	public static Logger logger = Logger.getLogger(ClientSocketTest.class);

	public Socket socket = null;
	public User user = null;
	public String roomId = null;

	public ClientSocketTest() throws UnknownHostException, IOException {

		user = new User();
		user.setId("100");
		user.setName("于志刚");
		user.setPassword("2019001");
		user.setStudentID("2019001");

	}

	public void test() throws UnknownHostException, IOException {

		socket = new Socket("127.0.0.1", 55533);
		new ClientReceiveThread();
		
		
		//登录
		LoginMessage loginMessage=new LoginMessage();
		loginMessage.setId(UUID.randomUUID().toString());
		loginMessage.setName(user.getName());
		loginMessage.setStudentID(user.getStudentID());
		loginMessage.setPassword(user.getPassword());
		//登录成功
		this.sendMessage(loginMessage);
	
		//登录失败
		loginMessage.setPassword("");
	    this.sendMessage(loginMessage);
			
	}

	public static void main(String args[]) throws Exception {
		ClientSocketTest clientSocketTest = new ClientSocketTest();
		clientSocketTest.test();
	}

	/**
	 * 发送消息
	 * 
	 * @param outputStream
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(IMessageProtocol msg) throws IOException {

		OutputStream outputStream = this.socket.getOutputStream();
		outputStream.write(msg.jsonByteLen() >> 8);
		outputStream.write(msg.jsonByteLen());
		outputStream.write(msg.jsonBytes());
		outputStream.flush();
		
		logger.info("<客户端发送-->消息> msg=\n"+ msg.toJsonString());

	}
	
	

	// 
	private class ClientReceiveThread implements Runnable {
		public ClientReceiveThread() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			try {
				while(true) {
					receiveMessage();
				}
			} catch (IOException e) {
				logger.error("receiveMessage出错：",e);
			}
			
		}
	}
	
	
	public boolean receiveMessage() throws IOException {
		InputStream inputStream = this.socket.getInputStream();
		int first = inputStream.read();
		// 如果读取的值为-1 说明到了流的末尾，Socket已经被关闭了，此时将不能再去读取
		if (first == -1) {
			//return false;
		}
		int second = inputStream.read();
		int length = (first << 8) + second;
		// 然后构造一个指定长的byte数组
		byte[] bytes = new byte[length];
		// 然后读取指定长度的消息即可
		inputStream.read(bytes);
		String strJson = new String(bytes, "UTF-8");

		logger.info("<客户端<--接收消息> msg=\n"+ strJson);

		JSONObject jo = JSONObject.parseObject(strJson);

		String type = jo.getString("type");

		switch (type) {

		case MessageType.MESSAGE_TYPE_LOGIN:
			LoginMessage loginMsg = JSON.toJavaObject(jo, LoginMessage.class);
			//logger.debug("receiveMessage json=" + strJson);

		case MessageType.MESSAGE_TYPE_TALK:
			TalkMessage talkMsg = JSON.toJavaObject(jo, TalkMessage.class);

		}
		
		return true;

	}

}
