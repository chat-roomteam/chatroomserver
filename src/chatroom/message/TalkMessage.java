package chatroom.message;

import chatroom.model.Talk;

public class TalkMessage extends BaseMessage {

	private String id;

	private Talk talk;
	
	private String roomId;

	public Talk getTalk() {
		return talk;
	}

	public void setTalk(Talk talk) {
		this.talk = talk;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	private String type = MessageType.MESSAGE_TYPE_TALK;

}
