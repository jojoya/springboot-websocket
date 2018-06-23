package com.example.demo.service;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



@ServerEndpoint("/socketServer/{userid}")
@Component
public class SocketServer {

    private Session session;
    private static Map<String,Session> sessionPool = new HashMap<>();
    private static Map<String,String> sessionIds = new HashMap<>();


    /**
     * 连接初次建立成功调用的方法*/
    @OnOpen
    public void open(Session session, @PathParam(value = "userid") String userid) {
        System.out.println(userid+"进入系统，sessionid为：" + session.getId() + ", session="+session);
        this.session = session;
        sessionPool.put(userid,session);     //加入set中
        sessionIds.put(session.getId(),userid);     //加入set中
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        String sessionid = session.getId();
        String userid = sessionIds.get(sessionid);
        System.out.println(userid+"，断开连接。。。");
        sessionPool.remove(userid);
        sessionIds.remove(sessionid);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的string、json消息*/
    @OnMessage
    public void onMessage(String message) {
        String sessionid = session.getId();
        String userid = sessionIds.get(sessionid);
        String newMessage = "发送人：" + userid  + ",\n sessionid：" + sessionid + ",\n 消息：stringType：：" + message;
            System.out.println(newMessage);
            sendAll(newMessage);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的type消息*/
    @OnMessage
    public void onMessage(byte[] message) {
        String sessionid = session.getId();
        String userid = sessionIds.get(sessionid);
        String newMessage = "发送人：" + userid  + ",\n sessionid：" + sessionid + ",\n 消息：byteType：：" + new String(message);
        System.out.println(newMessage);
        sendAll(newMessage);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }



    /**
     * 服务器发消息
     * */
    public static void sendMessage(String message,String userid){
        System.out.println("==server.sendMessage(),message:"+message+",userid:"+userid);

        Session s = sessionPool.get(userid);
        if(s!=null){
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public static int getOnlineNum() {
        return sessionPool.size();
    }

    public static String getOnlineUsers() {
        StringBuffer users = new StringBuffer();
        for (String key:sessionIds.keySet()
             ) {
            users.append(sessionIds.get(key) + ",");
        }
        return users.toString();
    }

    public static void sendAll(String msg) {

        System.out.println("==server.sendAll(),msg:"+msg);
        for (String key:sessionIds.keySet()){
            sendMessage(msg,sessionIds.get(key));
        }
    }


}