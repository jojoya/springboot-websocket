package com.example.demo.service;

import com.example.demo.protobuf.ProtoDemo;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
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
        System.out.println(userid+"，创建连接。。。\nsessionid：" + session.getId() + "\nsession="+session);
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
        /*String sessionid = session.getId();
        String userid = sessionIds.get(sessionid);
        String newMessage;
        //Json转protobuf
        ProtoDemo.Student.Builder builder =ProtoDemo.Student.newBuilder();
        try {
            JsonFormat.merge(message, builder);
        } catch (JsonFormat.ParseException e) {
            e.printStackTrace();
        }

        ProtoDemo.Student student = builder.build();
        //protobuf转Json
        String studentJson = JsonFormat.printToString(student);
        newMessage = "sender:" + userid  + "\n sessionid:" + sessionid + "\n StringMessage::" + studentJson;
        System.out.println("========server.rcv========");
        System.out.println(newMessage);*/

        sendAll(message);
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
    public void onMessage(byte[] message) throws InvalidProtocolBufferException{
       /* String sessionid = session.getId();
        String userid = sessionIds.get(sessionid);
        String newMessage = "sender:" + userid  + ",\n sessionid:" + sessionid + ",\n ByteMessage::" + new String(message);
*/
        ProtoDemo.Student  student = ProtoDemo.Student.parseFrom(message);

        //发消息：Json
/*        String studentJson = JsonFormat.printToString(student);
        String newMessage = "sender:" + userid  + "\n sessionid:" + sessionid + "\n ByteMessage::" + studentJson;
        System.out.println(newMessage);
        sendAll(studentJson);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //发消息：Binary
        ByteBuffer byteBuffer = ByteBuffer.wrap(student.toByteArray());
        sendBinaryAll(byteBuffer);
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

    /**
     * 服务器群发String消息
     * */
    public static void sendAll(String msg) {

        System.out.println("========server.sendAll========");
        System.out.println(msg);
        for (String key:sessionIds.keySet()){
            sendMessage(msg,sessionIds.get(key));
        }
    }

    /**
     * 服务器群发Binary消息
     * */
    public static void sendBinaryAll(ByteBuffer msg) {

        System.out.println("========server.sendBinaryAll========");
        System.out.println(msg);
        for (String key:sessionIds.keySet()){
            sendBinaryMessage(msg,sessionIds.get(key));
        }
    }


    /**
     * 服务器单发Binary消息
     * */
    public static void sendBinaryMessage(ByteBuffer message, String userid){
        System.out.println("========server.sendBinaryMessage()========");
        System.out.println(message+"\nuserid:"+userid);

        Session s = sessionPool.get(userid);
        if(s!=null){
            try {
                s.getBasicRemote().sendBinary(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 服务器单发String消息
     * */
    public static void sendMessage(String message,String userid){
        System.out.println("========server.sendMessage()========");
        System.out.println(message+"\nuserid:"+userid);

        Session s = sessionPool.get(userid);
        if(s!=null){
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}