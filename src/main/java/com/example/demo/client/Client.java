package com.example.demo.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;

import com.example.demo.protobuf.ProtoDemo;
import com.google.protobuf.InvalidProtocolBufferException;
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
            public void onMessage(String str) {
                System.out.println("client >>> 收到消息(String)；"+str);

                try {
                    Class obj = Class.forName("com.example.demo.client.Client");//注意此字符串必须是真实路径，就是带包名的类路径，包名.类名
//                    obj.getMethod()
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
                System.out.println("client >>> 收到消息(ByteBuffer)；"+String.valueOf(bytes));
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

        ProtoDemo.Student student = getStudent();
        byte[] student_byte = student.toByteArray();
        client.send(student_byte);

        ByteBuffer byteBuffer = ByteBuffer.wrap(student_byte);
        client.send(byteBuffer);

    }

    public static ProtoDemo.Student getStudent(){
        ProtoDemo.Student.PhoneNumber.Builder phoneNumberBuilder =  ProtoDemo.Student.PhoneNumber.newBuilder();
        phoneNumberBuilder.setNumber("15880000000");
        phoneNumberBuilder.setType(ProtoDemo.Student.PhoneType.MOBILE);
        ProtoDemo.Student.PhoneNumber phoneNumber = phoneNumberBuilder.build();

        ProtoDemo.Student.Builder builder = ProtoDemo.Student.newBuilder();
        builder.setId(100000);
        builder.setName("jmeter");
        builder.setEmail("jmeter@qq.com");
        builder.setSex(ProtoDemo.Student.Sex.MAN);
        builder.setPhone(phoneNumber);

        ProtoDemo.Student student = builder.build();

        System.out.println("===========student===========\n"+student);
        byte[] byteStudent = student.toByteArray();
        System.out.println("===========studentByte===========\n"+byteStudent);
        StringBuffer sb = new StringBuffer();
        for(byte b:student.toByteArray()){
            sb.append(b);
            System.out.print(b);
        }
//        System.out.println("\n===========studentByteStr===========\n"+student.toByteString());

        System.out.println("\n16:::"+bytesToHex(byteStudent));

        ProtoDemo.Student  studentBack = null;
        try {
            studentBack = ProtoDemo.Student.parseFrom(byteStudent);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        System.out.println("studentBack:\n"+studentBack);

        return student;
    }


    public static void send(byte[] bytes){
        client.send(bytes);
    }

    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
