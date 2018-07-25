package com.example.demo.jmeter;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by CaiXiaoLing on 2018/7/17.
 */
public class TestReflex {

    private  static byte[]  getBytes(String pbObj){
        try {
            Class clazz_protoDemo =  Class.forName(pbObj);
            System.out.println(clazz_protoDemo.getCanonicalName());

            Class clazz_student =  Class.forName(pbObj+"$Student");
            System.out.println(clazz_student.getCanonicalName());

            Class clazz_builder =  Class.forName(pbObj+"$Student$Builder");
            System.out.println(clazz_builder.getCanonicalName());

            Class clazz_phoneNumber =  Class.forName(pbObj+"$Student$PhoneNumber");
            System.out.println(clazz_phoneNumber.getCanonicalName());


            String pbClassName = pbObj+"$Student";
            Class cl = Class.forName(pbClassName);    // 首先根据 pb message class 名称得到 Class 对象。（坑1：注意内部类连接符为$,如 package_name.class_name$inner_class_name, 另外注意脚本启动的时候要注意对 $ 转义，否则 shell 会以为 $inner_class_name 为变量）
            Method method = cl.getMethod("newBuilder");    // newBuilder 为静态变量，即使没有 message 的具体实例也可以 invoke！yes！
            Object obj = method.invoke(null, null);
            Message.Builder msgBuilder = (Message.Builder)obj;       // 得到 builder
            Descriptors.Descriptor descriptor = msgBuilder.getDescriptorForType();   // 得到 descriptor



            Method[] methods = clazz_student.getMethods();
            for (Method m:methods
                 ) {
                if(m.getName().startsWith("parseFrom") && m.getName().equals("parseFrom"))
                System.out.println(m.getName());
            }

            Method m_newBuilder = clazz_student.getMethod("newBuilder");
            Method m_parseFrom = clazz_student.getMethod("parseFrom");

            //实例化一个Student对象
           /*  Object obj = clazz.getConstructor().newInstance();
//            m.invoke(obj, "刘德华");
            m.invoke(obj,null);*/

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } /*catch (InstantiationException e) {
            e.printStackTrace();
        }catch (NoSuchFieldException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    public static void main(String[] args) {
        getBytes("com.example.demo.protobuf.ProtoDemo");
    }

}
