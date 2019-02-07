package chatroom.message;

public class SessionCloseMessage extends BaseMessage{

	@Override
	public String getType() {
		return type;
	}
	
	private String type=MessageType.MESSAGE_TYPE_SESSION_COSLED;

}
