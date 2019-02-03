package chatroom.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;


public class Room implements Serializable{
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOnlineNumber() {
		return onlineNumber;
	}

	public void setOnlineNumber(int onlineNumber) {
		this.onlineNumber = onlineNumber;
	}

	public long getLastTalkTime() {
		return lastTalkTime;
	}

	public void setLastTalkTime(long lastTalkTime) {
		this.lastTalkTime = lastTalkTime;
	}
	
	public String toJsonString() {
		return JSON.toJSONString(this);
	}

	/**
	 * id 
	 */
	private String id;
	
	/**
	 * 名字
	 */
	private String name;
	
	/**
	 * 在线人数
	 */
	private int onlineNumber;
	
	/**
	 * 最后说话时间
	 */
	private long lastTalkTime;
	

}
