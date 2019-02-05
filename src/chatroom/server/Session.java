package chatroom.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import org.apache.log4j.Logger;

import chatroom.message.IMessageProtocol;

/**
 * 客户端的一次连接会话，断线重连后为一个新有session
 * 
 * @author felix
 *
 */

public class Session {

	private static Logger logger = Logger.getLogger(Session.class);
	private boolean isLogin = false;
    private Session selfSession;
	private MessageHandler messageHandler = new MessageHandler();
	
	private String sessionId=null;
	
	public String getSessionId() {
		return sessionId;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}


	/**
	 * 每个会话与一个socket对应
	 */
	private Socket client;

	public Session(Socket socket) {
		this.client = socket;
		this.sessionId=UUID.randomUUID().toString();
		selfSession=this;
		new ClientHandlerThread();
	}

	public Socket getClient() {
		return client;
	}


	// 客户端处理线程内部类，每个会话开启一个线程
	private class ClientHandlerThread implements Runnable {
		public ClientHandlerThread() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			logger.info("【启动一个会话线程】");
			InputStream inputStream = null;
			try {
				inputStream = client.getInputStream();
				// 不断处理请求
				while (true) {
					// 接收消息
					messageHandler.receiveMessage(selfSession );
				}
			} catch (IOException e) {
				logger.error("会话线程处理请求出错", e);
			}
		}

	}

}
