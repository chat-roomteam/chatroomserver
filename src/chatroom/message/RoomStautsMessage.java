package chatroom.message;

import java.util.List;

import chatroom.model.Talk;
import chatroom.model.User;

public class RoomStautsMessage extends BaseMessage {
	public String getType() {
		return type;
	}
	
	private String type=MessageType.MESSAGE_TYPE_ROOMSTAUTS;
	
    private String roomId;
	
	private Long timestamp;
	
	private List<User> users;
	
	private List<Talk> talks;
	
	
	public List<Talk> getTalks() {
		return talks;
	}

	public void setTalks(List<Talk> talks) {
		this.talks = talks;
	}

	

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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	
}
