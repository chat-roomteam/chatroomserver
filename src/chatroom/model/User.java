package chatroom.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class User implements Serializable {
	

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





	private String id;
	private String studentID;
	private String password;
	private String name;

	

	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
