package chatroom.message;

import chatroom.model.Talk;

public class TalkMessage extends BaseMessage {

	private String id;

	private Talk talk;

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

	@Override
	public String getType() {
		return this.type;
	}

	private String type = MessageType.MESSAGE_TYPE_TALK;

}
