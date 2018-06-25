package com.example.demo.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;

import com.example.demo.protobuf.ProtoDemo;
import org.java_websocket.WebSocket.READYSTATE;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

/**
 *Java实现websocket client端调用
 * */
public class Client {

    public static WebSocketClient client;

    public static void main(String[] args) throws URISyntaxException, NotYetConnectedException, UnsupportedEncodingException, InterruptedException {
        client = new WebSocketClient(new URI("ws://localhost:8080/socketServer/caixiaoling"),new Draft_6455()) {

            @Override
            public void onOpen(ServerHandshake arg0) {
                System.out.println("client >>> 打开链接");
            }

            @Override
            public void onMessage(String arg0) {
                System.out.println("client >>> 收到消息；"+arg0);
            }

            @Override
            public void onError(Exception arg0) {
                arg0.printStackTrace();
                System.out.println("client >>> 发生错误已关闭");
            }

            @Override
            public void onClose(int arg0, String arg1, boolean arg2) {
                System.out.println("client >>> 链接已关闭");
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                try {
                    System.out.println("client >>> " + new String(bytes.array(),"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


        };

        client.connect();

        while(!client.getReadyState().equals(READYSTATE.OPEN)){
            System.out.println("还没有打开");
            Thread.sleep(100);
        }
        System.out.println("打开了");
//        send("hello world".getBytes("utf-8"));
//        client.send("hello world String".getBytes("utf-8"));
//        client.send("hello world");
        client.send(getStudent().toByteArray());



    }

    public static ProtoDemo.Student getStudent() {
        ProtoDemo.Student.Builder builder = ProtoDemo.Student.newBuilder();
        builder.setId(1);
        builder.setName("caixiaoling");
        builder.setEmail("caixiaoling@qq.com");
        builder.setSex(ProtoDemo.Student.Sex.MAN);

        ProtoDemo.Student.PhoneNumber.Builder phoneNumberBuilder =  ProtoDemo.Student.PhoneNumber.newBuilder();
        phoneNumberBuilder.setNumber("15880000000");
        phoneNumberBuilder.setType(ProtoDemo.Student.PhoneType.MOBILE);
        ProtoDemo.Student.PhoneNumber phoneNumber = phoneNumberBuilder.build();
        builder.setPhone(phoneNumber);

        ProtoDemo.Student student = builder.build();
        /*System.out.println("===========student===========\n"+student);
        System.out.println("===========studentStr===========\n"+student.toString());
        System.out.println("===========studentByte===========\n"+student.toByteArray());
        for(byte b:student.toByteArray()){
            System.out.print(b);
        }
        System.out.println("\n===========studentByteStr===========\n"+student.toByteString());
*/
        return student;
    }


    public static void send(byte[] bytes){
        client.send(bytes);
    }
}
