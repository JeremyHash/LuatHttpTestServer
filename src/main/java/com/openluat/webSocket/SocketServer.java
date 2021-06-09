package com.openluat.webSocket;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

@Service
@Slf4j
@ServerEndpoint("/socket")
public class SocketServer {

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private ServerSocket server;

    private ArrayList<Socket> clientList = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose() {
    }

    @OnMessage
    public void onMessage(String message) throws IOException {

        log.info("receive ws msg:" + message);

        if (message.equals("OpenTcpServer")) {
            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    server = new ServerSocket(2999);
                    while (true) {
                        Socket client = null;
                        try {
                            client = server.accept();
                        } catch (IOException e) {
                            e.printStackTrace();

                            for (Socket socket : clientList) {
                                socket.close();
                            }
                            break;
                        }
                        clientList.add(client);
                        JSONObject clientInfoJsonObject = new JSONObject();
                        clientInfoJsonObject.put("ip", client.getInetAddress());
                        clientInfoJsonObject.put("port", client.getPort());
                        Socket finalClient = client;
                        new Thread(new Runnable() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                log.info("客户端:" + finalClient.getInetAddress() + "已连接到服务器");
                                JSONObject clientConnectJsonObject = new JSONObject();
                                clientConnectJsonObject.put("event", "clientConnect");
                                clientConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                String clientConnectMsg = clientConnectJsonObject.toJSONString();
                                session.getBasicRemote().sendText(clientConnectMsg);
                                BufferedReader br = new BufferedReader(new InputStreamReader(finalClient.getInputStream()));
                                while (true) {
                                    String msg = null;
                                    try {
                                        msg = br.readLine();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        JSONObject clientDisConnectJsonObject = new JSONObject();
                                        clientDisConnectJsonObject.put("event", "clientDisConnect");
                                        clientDisConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                        String clientDisconnectMsg = clientDisConnectJsonObject.toJSONString();
                                        session.getBasicRemote().sendText(clientDisconnectMsg);
                                        break;
                                    }
                                    if (msg == null) {
                                        log.info("客户端:" + finalClient.getInetAddress() + "下线");
                                        JSONObject clientDisConnectJsonObject = new JSONObject();
                                        clientDisConnectJsonObject.put("event", "clientDisConnect");
                                        clientDisConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                        String clientDisconnectMsg = clientDisConnectJsonObject.toJSONString();
                                        session.getBasicRemote().sendText(clientDisconnectMsg);
                                        break;
                                    }
                                    log.info("收到客户端消息：" + msg);
                                    JSONObject clientMessageJsonObject = new JSONObject();
                                    clientMessageJsonObject.put("event", "clientMsg");
                                    clientMessageJsonObject.put("data", msg);
                                    clientMessageJsonObject.put("clientInfo", clientInfoJsonObject);
                                    String jsonString = clientMessageJsonObject.toJSONString();
                                    session.getBasicRemote().sendText(jsonString);
                                }
                            }
                        }).start();
                    }
                }
            }).start();
        } else if (message.equals("CloseTcpServer")) {
            server.close();
            log.info("server status:" + server.isClosed());
        } else {
            log.info("ws receive data = " + message);

        }
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

}