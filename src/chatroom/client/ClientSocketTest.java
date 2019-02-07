package chatroom.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import chatroom.message.EnterRoomMessage;
import chatroom.message.ExitRoomMessage;
import chatroom.message.IMessageProtocol;
import chatroom.message.LoginMessage;
import chatroom.message.MessageType;
import chatroom.message.TalkMessage;
import chatroom.model.Talk;
import chatroom.model.User;

public class ClientSocketTest {

	public static Logger logger = Logger.getLogger(ClientSocketTest.class);

	public Socket socket = null;
	public User user = null;
	public String roomId = null;

	public ClientSocketTest(String studentID) throws UnknownHostException, IOException {

		user = new User();
		if ("2019001".equals(studentID)) {
			user.setId("100");
			user.setName("于志刚");
			user.setPassword("2019001");
			user.setStudentID("2019001");
		} else {
			user.setId("101");
			user.setName("王刚强");
			user.setPassword("2019002");
			user.setStudentID("2019002");
		}
	}

	public void test() throws UnknownHostException, IOException {

		socket = new Socket("127.0.0.1", 55533);
		new ClientReceiveThread();

		// 登录
		LoginMessage loginMessage = new LoginMessage();
		loginMessage.setId(UUID.randomUUID().toString());
		loginMessage.setName(user.getName());
		loginMessage.setStudentID(user.getStudentID());
		loginMessage.setPassword(user.getPassword());
		// 登录成功
		this.sendMessage(loginMessage);

		// 登录失败
		// loginMessage.setPassword("");
		// this.sendMessage(loginMessage);

		// 进入房间
		EnterRoomMessage enterRoomMessage = new EnterRoomMessage();
		enterRoomMessage.setRoomId("100");
		enterRoomMessage.setUser(user);
		this.sendMessage(enterRoomMessage);

		// 退出房间
		// ExitRoomMessage exitRoomMessage=new ExitRoomMessage();
		// exitRoomMessage.setRoomId("100");
		// exitRoomMessage.setUser(user);

		// 说话
		TalkMessage talkMessage = new TalkMessage();
		talkMessage.setId(UUID.randomUUID().toString());
		Talk talk = new Talk();
		talk.setId(UUID.randomUUID().toString());
		talk.setContent("我是" + user.getName() + ",我的学号是" + user.getStudentID() + ",现在时间是"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
		talk.setRoomId("100");
		talk.setStudentID(user.getStudentID());
		talk.setUserId(user.getId());
		talkMessage.setTalk(talk);
		
		this.sendMessage(talkMessage);

	}

	public static void main(String args[]) throws Exception {
		String studentID = "2019001";

		if (args.length > 0)
			studentID = args[0];

		ClientSocketTest clientSocketTest = new ClientSocketTest(studentID);

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

		logger.info("发送消息------>>>> type=" + msg.getType() + "  msg=" + msg.toJsonString());

	}

	//
	private class ClientReceiveThread implements Runnable {
		public ClientReceiveThread() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			try {
				while (true) {
					receiveMessage();
				}
			} catch (IOException e) {
				logger.error("receiveMessage出错：", e);
			}

		}
	}

	public boolean receiveMessage() throws IOException {
		InputStream inputStream = this.socket.getInputStream();
		int first = inputStream.read();
		// 如果读取的值为-1 说明到了流的末尾，Socket已经被关闭了，此时将不能再去读取
		if (first == -1) {
			// return false;
		}
		int second = inputStream.read();
		int length = (first << 8) + second;
		// 然后构造一个指定长的byte数组
		byte[] bytes = new byte[length];
		// 然后读取指定长度的消息即可
		inputStream.read(bytes);
		String strJson = new String(bytes, "UTF-8");

		JSONObject jo = JSONObject.parseObject(strJson);

		String type = jo.getString("type");

		logger.info("接收消息<<<------ type=" + type + " msg=" + strJson);

		switch (type) {

		case MessageType.MESSAGE_TYPE_LOGIN:
			LoginMessage loginMsg = JSON.toJavaObject(jo, LoginMessage.class);
			// logger.debug("receiveMessage json=" + strJson);

		case MessageType.MESSAGE_TYPE_TALK:
			TalkMessage talkMsg = JSON.toJavaObject(jo, TalkMessage.class);

		}

		return true;

	}

}
