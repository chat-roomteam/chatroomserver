package chatroom.model;
import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class Talk  implements Serializable{

	private String id;
	private long timestamp;
	private String userId;
	private String ip;
	private String roomId;
	private String content;
	
	public Talk() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String toJsonString() {
		return JSON.toJSONString(this);
	}

}
