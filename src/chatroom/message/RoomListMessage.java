package chatroom.message;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSON;

import chatroom.model.*;

public  class RoomListMessage  extends BaseMessage  {

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	private String type= MessageType.MESSAGE_TYPE_ROOMSLIST;
	
	@Override
	public String getType() {
		return type;
	}
	
	private List<Room> rooms;
	

}
