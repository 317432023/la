package com.jeetx.webSocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MyWebSocketClient extends WebSocketClient {

	Logger logger = Logger.getLogger(MyWebSocketClient.class);

	public MyWebSocketClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		logger.info("------ MyWebSocket onOpen ------");
	}

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		logger.info("------ MyWebSocket onClose ------" + arg1);
	}

	@Override
	public void onError(Exception arg0) {
		logger.info("------ MyWebSocket onError ------" + arg0.getMessage());
	}

	@Override
	public void onMessage(String arg0) {
		logger.info("-------- 接收到服务端数据： " + arg0 + "--------");
	}

	public static void main(String[] arg0) {
//		try {
//			//MyWebSocketClient myClient = new MyWebSocketClient(new URI("ws://159.138.22.154:6060/la-ws/webSocketServer?token=Ye0eQJWeKTShvSWnw3Yu8u0VSnw0VpAU&roomId=13"));
//			MyWebSocketClient myClient = new MyWebSocketClient(new URI("ws://127.0.0.1:8080/la-ws/webSocketServer?token=vpUF3uJAOiXbORi2dEKbHHqh3P3IXvPZ&roomId=13"));
//
//			myClient.connect();
//			while (!myClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
//				//System.out.println("还没有打开");
//			}
//			System.out.println("建立websocket连接");
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
		readTokenConnectWs();
	}
	
	public static void readTokenConnectWs() {
		String fileName = "E:/token.txt";
		//String fileName = "E:/tokenTest.txt";
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
            	final String token = tempString.replace(" ", "").replace("|", "");
            	System.out.println(line+"-"+token);
				new Thread(){
					public void run(){
		        		try {
			    			//MyWebSocketClient myClient = new MyWebSocketClient(new URI("ws://127.0.0.1:8080/la-ws/webSocketServer?token="+token+"&roomId=13"));
			    			MyWebSocketClient myClient = new MyWebSocketClient(new URI("ws://159.138.22.154:6060/la-ws/webSocketServer?token="+token+"&roomId=13"));
			    			myClient.connect();
			    			while (!myClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
			    				//System.out.println("还没有打开");
			    			}
			    			System.out.println(token+":建立websocket连接");
			    		} catch (Exception e) {
			    			e.printStackTrace();
			    		}
		        		
					};
				}.start();
    			Thread.sleep(3*1000);
				line++;
				if(line==100) {
					break;
				}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
}