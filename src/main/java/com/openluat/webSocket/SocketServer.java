package com.openluat.webSocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@ServerEndpoint("/socket")
public class SocketServer {

    private Session session;

    private ServerSocket server;

    private final HashMap<String, Socket> clientMap = new HashMap<>();

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
                        Socket client;
                        try {
                            client = server.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                            for (Map.Entry<String, Socket> entry : clientMap.entrySet()) {
                                entry.getValue().close();
                            }
                            break;
                        }
                        JSONObject clientInfoJsonObject = new JSONObject();
                        String clientIP = client.getInetAddress().toString().replaceAll("/", "");
                        int clientPort = client.getPort();
                        clientInfoJsonObject.put("ip", client.getInetAddress());
                        clientInfoJsonObject.put("port", client.getPort());
                        clientMap.put(clientIP + ":" + clientPort, client);
                        System.out.println("clientMap = " + clientMap);
                        Socket finalClient = client;
                        new Thread(new Runnable() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                JSONObject clientConnectJsonObject = new JSONObject();
                                clientConnectJsonObject.put("event", "clientConnect");
                                clientConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                String clientConnectMsg = clientConnectJsonObject.toJSONString();
                                session.getBasicRemote().sendText(clientConnectMsg);
                                BufferedReader br = new BufferedReader(new InputStreamReader(finalClient.getInputStream()));
                                while (true) {
                                    int readCount;
                                    String msg;
                                    char[] buff = new char[1024];
                                    readCount = br.read(buff);
                                    if (readCount == -1) {
                                        JSONObject clientDisConnectJsonObject = new JSONObject();
                                        clientDisConnectJsonObject.put("event", "clientDisConnect");
                                        clientDisConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                        String clientDisconnectMsg = clientDisConnectJsonObject.toJSONString();
                                        session.getBasicRemote().sendText(clientDisconnectMsg);
                                        break;
                                    } else {
                                        msg = new String(buff, 0, readCount);
                                    }
                                    JSONObject clientMessageJsonObject = new JSONObject();
                                    clientMessageJsonObject.put("event", "clientMsg");
                                    clientMessageJsonObject.put("data", msg);
                                    clientMessageJsonObject.put("clientInfo", clientInfoJsonObject);
                                    String jsonString = clientMessageJsonObject.toJSONString();
                                    synchronized (session) {
                                        session.getBasicRemote().sendText(jsonString);
                                    }
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
            HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(message, HashMap.class);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientMap.get(map.get("client")).getOutputStream()));
            bw.write(map.get("data"));
//            bw.close();
        }
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

}