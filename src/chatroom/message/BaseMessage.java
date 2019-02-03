package chatroom.message;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;

public abstract   class BaseMessage  implements IMessageProtocol {
	
	@Override
	public String toJsonString() {
		 return JSON.toJSONString(this);		 
	}

	 
	@Override
	public int jsonByteLen() throws UnsupportedEncodingException {
		byte[] bytes = toJsonString().getBytes("UTF-8");
		return bytes.length;
	}

	@Override
	public byte[] jsonBytes() throws UnsupportedEncodingException {
		return toJsonString().getBytes("UTF-8");
	}


	 

}
