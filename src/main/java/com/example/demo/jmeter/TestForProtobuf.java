/**
 * @Package com.ec.thrift.client
 * @Description: TODO
 * @author ecuser
 * @date 2016年11月11日 下午3:39:29
 */
package com.example.demo.jmeter;

import com.example.demo.protobuf.ProtoDemo;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static com.example.demo.client.Client.getStudent;


/**
 * @ClassName: QurryForWeb
 * @Description: TODO
 * @author ecuser
 * @date 2016年11月11日 下午3:39:29
 */
public class TestForProtobuf extends AbstractJavaSamplerClient {

    private static WebSocketClient client;
    private static String result_str = null;
    private static byte[] result_byte = null;

    private  void initClient(String url) {
        try {
            client = new WebSocketClient(new URI(url), new Draft_6455()) {

                @Override
                public void onOpen(ServerHandshake arg0) {
                    getLogger().info("打开连接.");
                }

                @Override
                public void onError(Exception arg0) {
                    arg0.printStackTrace();
                    getLogger().error("错误导致连接断开...");
                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {
                    getLogger().info("连接已关闭");
                }

                @Override
                public void onMessage(String msg) {
                    result_str = msg;
                    getLogger().info("收到消息(String)");
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    getLogger().info("收到消息(ByteBuffer)");
                    result_byte = bytes.array();

                  /* String strMsg = String.valueOf(bytes);
                    try {
                        getLogger().info(new String(bytes.array(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                }


            };
        } catch (URISyntaxException e) {
            getLogger().error(e.getMessage());
        }
    }

    public org.apache.log.Logger getLogger(){
        return super.getLogger();
    }



    private static byte[] getSendBytes(String msg,String className) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //反射
        Class cl = Class.forName(className);    // 首先根据 pb message class 名称得到 Class 对象。（坑1：注意内部类连接符为$,如 package_name.class_name$inner_class_name, 另外注意脚本启动的时候要注意对 $ 转义，否则 shell 会以为 $inner_class_name 为变量）
        Method method = cl.getMethod("newBuilder");    // newBuilder 为静态变量，即使没有 message 的具体实例也可以 invoke！yes！
        Object obj = method.invoke(null, null);
//        Message.Builder builder = (Message.Builder)obj;       // 得到 builder
        GeneratedMessage.Builder builder = (GeneratedMessage.Builder)obj;
//        GeneratedMessage.class.isAssignableFrom(clazz);

        try {
            JsonFormat.merge(msg, builder);
        } catch (JsonFormat.ParseException e) {
            e.printStackTrace();
        }
        return builder.build().toByteArray();
    }

    private static String getResult(byte[] bytes,String className) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        //反射
         Class cl = Class.forName(className);    // 首先根据 pb message class 名称得到 Class 对象。（坑1：注意内部类连接符为$,如 package_name.class_name$inner_class_name, 另外注意脚本启动的时候要注意对 $ 转义，否则 shell 会以为 $inner_class_name 为变量）
        Method method = cl.getMethod("parseFrom",byte[].class);    // newBuilder 为静态变量，即使没有 message 的具体实例也可以 invoke！yes！
        Object obj = method.invoke(method, bytes);
//        Message message = (Message)obj;
        GeneratedMessage message = (GeneratedMessage)obj;
        return JsonFormat.printToString(message);
    }

    // 执行测试方法
    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        String msg = arg0.getParameter("msg");
        String type = arg0.getParameter("type");
        String sendMsgPackage = arg0.getParameter("sendMsgPackage");
        String sendMsgClass = arg0.getParameter("sendMsgClass");
        String rcvMsgPackage = arg0.getParameter("rcvMsgPackage");
        String rcvMsgClass = arg0.getParameter("rcvMsgClass");

        SampleResult sr = new SampleResult();

        try {
            sr.sampleStart();// jmeter 开始统计响应时间标记

            //发送text消息
            if(type.equals("text")){
                client.send(msg);
            }

            //发送Binary消息
            if(type.equals("binary")){
                String className = sendMsgPackage + "." + sendMsgClass;
                byte[] byte_msg = getSendBytes(msg,className);
                client.send(byte_msg);
            }

            while(result_str==null&&result_byte==null){
                getLogger().info("消息接收中...");
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    getLogger().error(e.toString());
                }
            }

            String result;
            if(result_str!=null){
                result = result_str;
            }else if(result_byte!=null){
                String className = rcvMsgPackage + "." + rcvMsgClass;
                result = getResult(result_byte,className);
            }else{
                result = "返回的数据很奇怪！";
            }

           System.out.println(result);

         // 通过下面的操作就可以将被测方法的响应输出到Jmeter的察看结果树中的响应数据里面了。
            sr.setResponseData("结果是：" + result, "UTF-8");
            sr.setDataType(SampleResult.TEXT);
            sr.setSuccessful(true);
        } catch (Throwable e) {
            sr.setSuccessful(false);
            e.printStackTrace();
        } finally {
            sr.sampleEnd();// jmeter 结束统计响应时间标记
        }

        return sr;
    }

    // 每个线程测试前执行一次，做一些初始化工作；
    @Override
    public void setupTest(JavaSamplerContext arg0) {
        String url = arg0.getParameter("url");
        initClient(url);
        client.connect();

        while(!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
            getLogger().info("连接中...");
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                getLogger().error(e.toString());
            }
        }

        getLogger().info("连接成功...");
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("url", "");
        params.addArgument("msg", "");
        params.addArgument("type", "");
        params.addArgument("sendMsgPackage", "");
        params.addArgument("sendMsgClass", "");
        params.addArgument("rcvMsgPackage", "");
        params.addArgument("rcvMsgClass", "");
        return params;
    }

    //测试结束时调用；
    @Override
    public void teardownTest(JavaSamplerContext arg0) {
        client.close();
    }

   	// main只是为了调试用，最后打jar包的时候注释掉。
    public static void main(String[] args)
    {
        ProtoDemo.Student student = getStudent();
        String studentJson = JsonFormat.printToString(student);

        Arguments params = new Arguments();
        params.addArgument("url", "ws://localhost:8080/socketServer/caixiaoling");//设置参数，
        params.addArgument("msg", studentJson);
        params.addArgument("type", "binary");//设置参数 binary、text
        params.addArgument("sendMsgPackage", "com.example.demo.protobuf");
        params.addArgument("sendMsgClass", "ProtoDemo$Student");
        params.addArgument("rcvMsgPackage", "com.example.demo.protobuf");
        params.addArgument("rcvMsgClass", "ProtoDemo$Student");

        JavaSamplerContext arg0 = new JavaSamplerContext(params);
        TestForProtobuf  ht = new TestForProtobuf();
        ht.setupTest(arg0);
        ht.runTest(arg0);
        ht.teardownTest(arg0);
    }
}
