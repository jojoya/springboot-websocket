package com.example.demo.client;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;

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
        send("hello world".getBytes("utf-8"));
        client.send("hello world");
    }

    public static void send(byte[] bytes){
        client.send(bytes);
    }
}
