package chatroom.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import chatroom.message.IMessageProtocol;


/**
 * 聊天室服务
 * @author felix
 *
 */
public class ServerApp {
	
	
    private ServerContext serverContext=ServerContext.getInstance();
	private static Logger logger = Logger.getLogger(ServerApp.class);


    /**
     * 启动
     * @throws IOException 
     */
	public void start() throws IOException {
		logger.info("聊天室服务启动");
		int port = 55533;
		ServerSocket server = new ServerSocket(port);
		logger.info("聊天室 socketServer 启动，端口号:"+55533);
		
		serverContext.initServer(server);	
 
	}


	public static void main(String[] args) {		 
		try {
			new ServerApp().start();
		} catch (IOException e) {
			 logger.error("服务出错",e);
		}
	}
}
