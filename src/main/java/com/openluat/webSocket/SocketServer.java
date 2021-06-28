package com.openluat.webSocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Service
@Slf4j
@ServerEndpoint("/socket")
public class SocketServer {

    private Session session;

    private ServerSocket server;

    private int serverPort;

    private final HashMap<String, Socket> clientMap = new HashMap<>();
    private final HashMap<Integer, BufferedWriter> serverLogWriterMap = new HashMap<>();

    public String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose() {
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        if (message.equals("OpenTcpServer")) {
            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    for (; ; ) {
                        int randomNum = new Random().nextInt(50);
                        serverPort = randomNum + 2950;
                        try {
                            server = new ServerSocket(serverPort);
                            File file = new File("static/" + serverPort + ".txt");
                            if (file.exists()) {
                                file.delete();
                            }
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                            serverLogWriterMap.put(serverPort, bufferedWriter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                        JSONObject serverPortJsonObject = new JSONObject();
                        serverPortJsonObject.put("event", "openTcpServerPort");
                        serverPortJsonObject.put("port", serverPort);
                        String serverPortMsg = serverPortJsonObject.toJSONString();
                        session.getBasicRemote().sendText(serverPortMsg);
                        break;
                    }
                    while (true) {
                        Socket client;
                        try {
                            client = server.accept();
                        } catch (SocketException e) {
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
                        new Thread(new Runnable() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                JSONObject clientConnectJsonObject = new JSONObject();
                                clientConnectJsonObject.put("event", "clientConnect");
                                clientConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                String clientConnectMsg = clientConnectJsonObject.toJSONString();
                                session.getBasicRemote().sendText(clientConnectMsg);
                                serverLogWriterMap.get(serverPort).write(getCurrentTime() + "-" + clientIP + ":" + clientPort + "-client: connect\n");
                                serverLogWriterMap.get(serverPort).flush();
                                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                while (true) {
                                    int readCount = 0;
                                    String msg;
                                    char[] buff = new char[1024];
                                    try {
                                        readCount = br.read(buff);
                                    } catch (SocketException e) {
                                        e.printStackTrace();
                                        JSONObject clientDisConnectJsonObject = new JSONObject();
                                        clientDisConnectJsonObject.put("event", "clientDisConnect");
                                        clientDisConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                        String clientDisconnectMsg = clientDisConnectJsonObject.toJSONString();
                                        session.getBasicRemote().sendText(clientDisconnectMsg);
                                        serverLogWriterMap.get(serverPort).write(getCurrentTime() + "-" + clientIP + ":" + clientPort + "-client: disconnect\n");
                                        serverLogWriterMap.get(serverPort).flush();
                                        break;
                                    }
                                    if (readCount == -1) {
                                        JSONObject clientDisConnectJsonObject = new JSONObject();
                                        clientDisConnectJsonObject.put("event", "clientDisConnect");
                                        clientDisConnectJsonObject.put("clientInfo", clientInfoJsonObject);
                                        String clientDisconnectMsg = clientDisConnectJsonObject.toJSONString();
                                        session.getBasicRemote().sendText(clientDisconnectMsg);
                                        serverLogWriterMap.get(serverPort).write(getCurrentTime() + "-" + clientIP + ":" + clientPort + "-client: disconnect\n");
                                        serverLogWriterMap.get(serverPort).flush();
                                        break;
                                    } else if (readCount > 0) {
                                        msg = new String(buff, 0, readCount);
                                    } else {
                                        continue;
                                    }
                                    JSONObject clientMessageJsonObject = new JSONObject();
                                    clientMessageJsonObject.put("event", "clientMsg");
                                    clientMessageJsonObject.put("data", msg);
                                    clientMessageJsonObject.put("clientInfo", clientInfoJsonObject);
                                    String jsonString = clientMessageJsonObject.toJSONString();
                                    synchronized (session) {
                                        session.getBasicRemote().sendText(jsonString);
                                    }
                                    serverLogWriterMap.get(serverPort).write(getCurrentTime() + "-" + clientIP + ":" + clientPort + "-client: " + msg + "\n");
                                    serverLogWriterMap.get(serverPort).flush();
                                }
                            }
                        }).start();
                    }
                }
            }).start();
        } else if (message.equals("CloseTcpServer")) {
            server.close();
        } else {
            HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(message, HashMap.class);
            String data = map.get("data");
            String isHex = map.get("isHex");
            if (isHex.equals("true")) {
                OutputStream outputStream = clientMap.get(map.get("client")).getOutputStream();
                outputStream.write(hexToByteArray(data));
                outputStream.flush();
            } else {
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientMap.get(map.get("client")).getOutputStream()));
                bufferedWriter.write(data);
                bufferedWriter.flush();
            }
            serverLogWriterMap.get(serverPort).write(getCurrentTime() + "-" + map.get("client") + "-server: " + data + "\n");
            serverLogWriterMap.get(serverPort).flush();
        }
    }

    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            // 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static String bytesToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte b : bArray) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

}