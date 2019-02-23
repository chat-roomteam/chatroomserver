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
import chatroom.message.RoomStautsMessage;
import chatroom.message.TalkMessage;
import chatroom.model.Room;
import chatroom.model.Talk;
import chatroom.model.User;

/**
 * 消息处理器
 * 
 * @author felix
 *
 */
public class MessageHandler {
	public Room room;
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

			loginMessage.setName(user.getName());

			loginMessage.setPassword(null);
			// 发送登录成功信息
			sendMessage(session, loginMessage);

			logger.info("【登录成功】-->user name=" + loginMessage.getName() + " 学号=" + loginMessage.getStudentID());

			// 再发送房间清单
			/*
			 * RoomListMessage roomListMessage = new RoomListMessage();
			 * 
			 * List<Room> list = new ArrayList<Room>();
			 * list.addAll(serverContext.rooms.values()); roomListMessage.setRooms(list);
			 * 
			 * sendMessage(session, roomListMessage);
			 */

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
		room = this.serverContext.rooms.get(roomId);

		User user = enterRoomMessage.getUser();

		if (room != null) {
			room.setOnlineNumber(room.getOnlineNumber() + 1);
		}

		// 获得了房间的用户清单
		serverContext.roomUsers.put(roomId,new ArrayList());
		List<User> userList = serverContext.roomUsers.get(roomId);
		// 增加用户
		userList.add(user);

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
		String roomId = room.getId();
		//Room room = this.serverContext.rooms.get(roomId);
		// 更新在线人数
		if (room != null) {
			room.setOnlineNumber(room.getOnlineNumber() - 1 < 0 ? 0 : room.getOnlineNumber() - 1);
		}

		User user = exitRoomMessage.getUser();

		// 获得了房间的用户清单
		serverContext.roomUsers.put(roomId,new ArrayList());
		List<User> userList = serverContext.roomUsers.get(roomId);
		// 删除用户
		userList.remove(user);

		logger.info(
				"【退出房间】-->user name=" + exitRoomMessage.getUser().getName() 
						+ " roomId=" + roomId + " 在线人数=" + room.getOnlineNumber());
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
			serverContext.roomtalks.put(roomId,new ArrayList());
			this.serverContext.roomtalks.get(roomId).add(talkMessage.getTalk());
		}

		// 广播给所有的客户端
		this.broadcastMessage(talkMessage);

		logger.info("【说话】-->user name=" + talkMessage.getTalk().getUserName() + " room id="
				+ room.getId() + " 内容：" + talkMessage.getTalk().getContent());

	}

	/**
	 * 广播消息
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void broadcastMessage(IMessageProtocol message) throws IOException {
		logger.debug("【广播消息】 类型=" + message.getType());
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

		logger.debug("【发送消息】类型=" + msg.getType() + " msg=" + msg.toJsonString());

	}

	/**
	 * 接受消息
	 * 
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

			logger.debug("【服务器收到消息】type=" + type + "消息内容=" + strJson);

			switch (type) {

			// 登录
			case MessageType.MESSAGE_TYPE_LOGIN:
				LoginMessage loginMsg = JSON.toJavaObject(jo, LoginMessage.class);
				loginMsg.setTimestamp(System.currentTimeMillis());
				this.login(session, loginMsg);
				break;
			// 请求房间列表
			case MessageType.MESSAGE_TYPE_ROOMSLIST:
				RoomListMessage roomListMsg = new RoomListMessage();
				List<Room> list = new ArrayList<Room>();
				list.addAll(serverContext.rooms.values());
				roomListMsg.setRooms(list);
				sendMessage(session, roomListMsg);
				break;
			// 进入房间
			case MessageType.MESSAGE_TYPE_ENTERROOM:
				EnterRoomMessage enterRoomMsg = JSON.toJavaObject(jo, EnterRoomMessage.class);
				enterRoomMsg.setTimestamp(System.currentTimeMillis());
				this.enterRoom(session, enterRoomMsg);
				break;
			// 请求房间状态
			case MessageType.MESSAGE_TYPE_ROOMSTAUTS:
				RoomStautsMessage roomStautsMsg = JSON.toJavaObject(jo, RoomStautsMessage.class);
				roomStautsMsg.setTimestamp(System.currentTimeMillis());
				String roomId=roomStautsMsg.getRoomId();
				//房间用户清单
				roomStautsMsg.setUsers(serverContext.roomUsers.get(roomId));
				//对话清单
				roomStautsMsg.setTalks(serverContext.roomtalks.get(roomId));
				
				sendMessage(session, roomStautsMsg);
				break;
			// 退出房间
			case MessageType.MESSAGE_TYPE_EXITROOM:
				ExitRoomMessage exitRoomMsg = JSON.toJavaObject(jo, ExitRoomMessage.class);
				this.exitRoom(session, exitRoomMsg);
				break;
			// 说话
			case MessageType.MESSAGE_TYPE_TALK:
				TalkMessage talkMsg = JSON.toJavaObject(jo, TalkMessage.class);
				Talk talk=talkMsg.getTalk();
				talk.setTimestamp(System.currentTimeMillis());
				
				//List<Talk> talks=serverContext.roomtalks.get(talkRoomId);
				//talks.add(talk);
				this.talk(session, talkMsg);
			}

		} catch (IOException e) {

			logger.error("receiveMessage出错：", e);
			serverContext.closeSession(session);
			session.getClient().close();

		}

	}
}
