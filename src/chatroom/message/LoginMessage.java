package chatroom.message;

public class LoginMessage extends BaseMessage {

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(int loginTimes) {
		this.loginTimes = loginTimes;
	}

	public boolean getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(boolean loginResult) {
		this.loginResult = loginResult;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	private String type = MessageType.MESSAGE_TYPE_LOGIN;
	private String id;
	private String studentID;
	private String password;
	private String name;
	private int loginTimes = 0;
	private boolean loginResult = false;
	private long timestamp;

	@Override
	public String getType() {
		return type;
	}

}
