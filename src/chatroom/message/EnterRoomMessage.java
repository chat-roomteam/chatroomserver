package chatroom.message;

import chatroom.model.User;

public class EnterRoomMessage  extends BaseMessage{

	@Override
	public String getType() {
		return type;
	}
	
	private String type=MessageType.MESSAGE_TYPE_ENTERROOM;
	
	private String roomId;
	
	private Long timestamp;
	
	private User user;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
