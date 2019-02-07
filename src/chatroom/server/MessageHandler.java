package chatroom.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import chatroom.message.EnterRoomMessage;
import chatroom.message.ExitRoomMessage;
import chatroom.message.IMessageProtocol;
import chatroom.message.LoginMessage;
import chatroom.message.MessageType;
import chatroom.message.RoomListMessage;
import chatroom.message.TalkMessage;
import chatroom.model.Room;
import chatroom.model.User;

/**
 * 消息处理器
 * 
 * @author felix
 *
 */
public class MessageHandler {

	private static Logger logger = Logger.getLogger(MessageHandler.class);

	private ServerContext serverContext = ServerContext.getInstance();

	/**
	 * 登录
	 * 
	 * @param session
	 * @param loginMessage
	 * @throws IOException
	 */
	public void login(Session session, LoginMessage loginMessage) throws IOException {

		// 核对密码

		String studentID = loginMessage.getStudentID();
		User user = serverContext.users.get(studentID);

		if (user != null && loginMessage.getPassword().equals(user.getPassword())) {

			loginMessage.setLoginResult(true);
			// 设置会话登录状态
			session.setLogin(true);
			loginMessage.setPassword(null);
			// 发送登录成功信息
			sendMessage(session, loginMessage);

			logger.info("【登录成功】-->user name=" + loginMessage.getName() + " 学号=" + loginMessage.getStudentID());

			// 再发送房间清单
			RoomListMessage roomListMessage = new RoomListMessage();

			List<Room> list = new ArrayList<Room>();
			list.addAll(serverContext.rooms.values());
			roomListMessage.setRooms(list);

			sendMessage(session, roomListMessage);

		} else {
			loginMessage.setLoginResult(false);
			loginMessage.setPassword(null);
			loginMessage.setLoginTimes(loginMessage.getLoginTimes() + 1);

			sendMessage(session, loginMessage);
			logger.info("【登录失败】-->user name=" + loginMessage.getName() + " 学号=" + loginMessage.getStudentID());
		}
	}

	/**
	 * 进入房间
	 * 
	 * @param session
	 * @param enterRoomMessage
	 * @throws IOException
	 */
	public void enterRoom(Session session, EnterRoomMessage enterRoomMessage) throws IOException {
		String roomId = enterRoomMessage.getRoomId();
		Room room = this.serverContext.rooms.get(roomId);

		if (room != null) {
			room.setOnlineNumber(room.getOnlineNumber() + 1);
		}

		logger.info("【进入房间】-->user name=" + enterRoomMessage.getUser().getName() + " roomId="
				+ enterRoomMessage.getRoomId() + " 在线人数=" + room.getOnlineNumber());

		// 广播
		this.broadcastMessage(enterRoomMessage);
	}

	/**
	 * 退出房间
	 * 
	 * @param session
	 * @param enterRoomMessage
	 * @throws IOException
	 */
	public void exitRoom(Session session, ExitRoomMessage exitRoomMessage) throws IOException {
		String roomId = exitRoomMessage.getRoomId();
		Room room = this.serverContext.rooms.get(roomId);
		// 更新在线人数
		if (room != null) {
			room.setOnlineNumber(room.getOnlineNumber() - 1 < 0 ? 0 : room.getOnlineNumber() - 1);
		}

		logger.info(
				"【退出房间】-->user name=" + exitRoomMessage.getUser().getName() + " roomId=" + exitRoomMessage.getRoomId()
						+ " roomId=" + exitRoomMessage.getRoomId() + " 在线人数=" + room.getOnlineNumber());
		// 广播
		this.broadcastMessage(exitRoomMessage);
	}

	/**
	 * 对话消息
	 * 
	 * @param session
	 * @param talkMessage
	 * @throws IOException
	 */
	public void talk(Session session, TalkMessage talkMessage) throws IOException {
		// 房间id
		String roomId = talkMessage.getTalk().getRoomId();

		if (serverContext.rooms.get(roomId) != null) {
			// 更新房间最后说话时间
			serverContext.rooms.get(roomId).setLastTalkTime(talkMessage.getTalk().getTimestamp());
		}

		if (null != this.serverContext.roomtalks.get(roomId)) {
			// 加入到访问的对话list中
			this.serverContext.roomtalks.get(roomId).add(talkMessage.getTalk());
		}

		// 广播给所有的客户端
		this.broadcastMessage(talkMessage);

		logger.info("【说话】-->user name=" + talkMessage.getTalk().getUserName() + " room id="
				+ talkMessage.getTalk().getRoomId() + " 内容：" + talkMessage.getTalk().getContent());

	}

	/**
	 * 广播消息
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void broadcastMessage(IMessageProtocol message) throws IOException {
		for (Session session : serverContext.sessionList) {
			sendMessage(session, message);
		}
	}

	public void sendMessage(Session session, IMessageProtocol msg) throws IOException {
		// 如果socket已关闭
		if (session.getClient() == null)
			return;

		OutputStream outputStream = session.getClient().getOutputStream();
		outputStream.write(msg.jsonByteLen() >> 8);
		outputStream.write(msg.jsonByteLen());
		outputStream.write(msg.jsonBytes());
		outputStream.flush();

	}

	/**
	 * 接受消息
	 * @param session
	 * @throws IOException
	 */
	public void receiveMessage(Session session) throws IOException {
		try {
			InputStream inputStream = session.getClient().getInputStream();
			int first = inputStream.read();

			// 如果读取的值为-1 说明到了流的末尾，Socket已经被关闭了，此时将不能再去读取
			if (first == -1) {
				// 主动关闭已方socket
				session.getClient().close();
				serverContext.closeSession(session);
				// 处理session相关的用户状态
				//
				return;
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
			
			logger.debug("【服务器收到消息】type="+type +"消息内容=" + strJson);
			
			switch (type) {
			
			//登录
			case MessageType.MESSAGE_TYPE_LOGIN:
				LoginMessage loginMsg = JSON.toJavaObject(jo, LoginMessage.class);
				loginMsg.setTimestamp(System.currentTimeMillis());
				this.login(session, loginMsg);
				break;
				//进入房间
			case MessageType.MESSAGE_TYPE_ENTERROOM:
				EnterRoomMessage enterRoomMsg=  JSON.toJavaObject(jo, EnterRoomMessage.class);
				enterRoomMsg.setTimestamp(System.currentTimeMillis());
				this.enterRoom(session, enterRoomMsg);
				break;
			//退出房间
				case MessageType.MESSAGE_TYPE_EXITROOM:
					ExitRoomMessage exitRoomMsg=  JSON.toJavaObject(jo, ExitRoomMessage.class);
					exitRoomMsg.setTimestamp(System.currentTimeMillis());
					this.exitRoom(session, exitRoomMsg);
					break;
		    //说话
			case MessageType.MESSAGE_TYPE_TALK:
				TalkMessage talkMsg = JSON.toJavaObject(jo, TalkMessage.class);
				// talkMsg.getTalk().setTimestamp(System.currentTimeMillis());

			}

		} catch (IOException e) {

			logger.error("receiveMessage出错：", e);
			serverContext.closeSession(session);
			session.getClient().close();

		}

	}
}
