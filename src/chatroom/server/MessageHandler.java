package chatroom.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import chatroom.message.IMessageProtocol;
import chatroom.message.LoginMessage;
import chatroom.message.MessageType;
import chatroom.message.TalkMessage;


/**
 * 消息处理器
 * @author felix
 *
 */
public class MessageHandler {

	private static Logger logger = Logger.getLogger(MessageHandler.class);

	private ServerContext serverContext = ServerContext.getInstance();

	// 登录
	public void login(Session session, LoginMessage loginMessage) throws IOException {
		if (serverContext.users.containsKey(loginMessage.getStudentID()) && 
				loginMessage.getPassword() == serverContext.users.get(loginMessage.getStudentID()).getPassword()  ) {
			loginMessage.setLoginResult(true);
			sendMessage(session,loginMessage);
			
			//再发送房间清单
			sendMessage(session,loginMessage);
			
		}else {
			loginMessage.setLoginResult(false);
			sendMessage(session,loginMessage);
		}
	}

	public void broadcastMessage(IMessageProtocol message) {

	}

	public void sendMessage(Session session, IMessageProtocol msg) throws IOException {

		OutputStream outputStream = session.getClient().getOutputStream();
		outputStream.write(msg.jsonByteLen() >> 8);
		outputStream.write(msg.jsonByteLen());
		outputStream.write(msg.jsonBytes());
		outputStream.flush();

	}

	public void receiveMessage(Session session) throws IOException {
		InputStream inputStream = session.getClient().getInputStream();
		int first = inputStream.read();
		// 如果读取的值为-1 说明到了流的末尾，Socket已经被关闭了，此时将不能再去读取
		if (first == -1) {
			return;
		}
		int second = inputStream.read();
		int length = (first << 8) + second;
		// 然后构造一个指定长的byte数组
		byte[] bytes = new byte[length];
		// 然后读取指定长度的消息即可
		inputStream.read(bytes);
		String strJson = new String(bytes, "UTF-8");
		logger.debug("receiveMessage json=" + strJson);
		JSONObject jo = JSONObject.parseObject(strJson);
		String type = jo.getString("type");

		switch (type) {
		case MessageType.MESSAGE_TYPE_TALK:

		}

	}

}
