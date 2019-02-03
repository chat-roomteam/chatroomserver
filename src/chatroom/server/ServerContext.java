package chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;

import chatroom.common.FileUtils;
import chatroom.model.Room;
import chatroom.model.Talk;
import chatroom.model.User;

/**
 * 聊天服务上下文，会话状态、 一个服务只有一个Context,采用单例模式
 * 
 * @author felix
 *
 */

public class ServerContext {
	private static Logger logger = Logger.getLogger(ServerContext.class);
	private final static ServerContext instance = new ServerContext();
	
	//public MessageHandler messageHandler=new MessageHandler();

	// 服务端ServerSocket
	private ServerSocket server;
	
	//所有房间 roomId,room
	public Map<String,Room> rooms=new HashMap<String,Room>();
	
	//所有用户 StudentID，user
	public Map<String,User> users=new HashMap<String,User>();
	
	//所有房间的对话 roomId List
	public Map<String,List<Talk>> roomtalks=new HashMap();
	
	// 所有会话
	public List<Session> sessionList = new ArrayList();
 

	/**
	 * 私有构造器，防止外部实例化
	 */
	private ServerContext() {

	}

	public static ServerContext getInstance() {
		return instance;
	}

	public void initServer(ServerSocket serverSocket) throws IOException {
		this.server = serverSocket;
		initChatRooms();
		initUsers();

		while (true) {
			// 从请求队列中取出一个连接
			Socket client = serverSocket.accept();
			// 创建session,处理这次连接
			openSession(client);
		}
	}

	/**
	 * 从服务器配置文件中读取房间清单
	 */
	private void initChatRooms() {
		try {
			String str = FileUtils.readJsonFile("./data/rooms.json");
			
			List<Room> roomList=new ArrayList<Room>(JSONArray.parseArray(str,Room.class));
			
			for(Room room : roomList) { 
				rooms.put(room.getId(), room);
				
			    //System.out.println(room.toJsonString());
			}
			
			logger.debug("init rooms, size= : " + roomList.size());
		} catch (IOException e) {
			logger.error("initChatRooms error: ", e);
		}
	}
	
	/**
	 * 从服务器配置文件中读取房间清单
	 */
	private void initUsers() {
		try {
			String str = FileUtils.readJsonFile("./data/users.json");
			
			List<User> userList=new ArrayList<User>(JSONArray.parseArray(str,User.class));
			
			for(User user : userList) { 
				users.put(user.getStudentID(), user);
			    //System.out.println(user.toJsonString());
			}
			
			logger.debug("init users, size= : " + userList.size());
		} catch (IOException e) {
			logger.error("initUsers error: ", e);
		}
	}

	
	/**
	 * 打开会话
	 */
	public void openSession(Socket client) {
		Session sessionNew = new Session(client);
		sessionList.add(sessionNew);
	}

	/**
	 * 关闭会话
	 */
	public void closeSession(Session session) {
		sessionList.remove(session);
	}

}
