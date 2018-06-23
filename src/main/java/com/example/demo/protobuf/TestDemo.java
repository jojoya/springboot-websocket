package com.example.demo.protobuf;

/**
 * Created by CaiXiaoLing on 2018/6/22.
 */
public class TestDemo {
    public static void main(String[] args) {
        ProtoDemo.Student.Builder builder = ProtoDemo.Student.newBuilder();
        builder.setId(1);
        builder.setName("caixiaoling");
        builder.setEmail("caixiaoling@qq.com");
        builder.setSexValue(ProtoDemo.Student.Sex.WOMAN.getNumber());

//        System.out.println("Man:"+ProtoDemo.Student.Sex.MAN.getNumber());

        ProtoDemo.Student.PhoneNumber.Builder phoneNumberBuilder =  ProtoDemo.Student.PhoneNumber.newBuilder();
        phoneNumberBuilder.setNumber("15880000000");
        phoneNumberBuilder.setTypeValue(ProtoDemo.Student.PhoneType.HOME.getNumber());
        ProtoDemo.Student.PhoneNumber phoneNumber = phoneNumberBuilder.build();
        builder.setPhone(phoneNumber);

        ProtoDemo.Student student = builder.build();
        System.out.println("student:"+student);
        System.out.println("studentStr:"+student.toString());
        System.out.println("studentByte:"+student.toByteArray());
        System.out.println("studentByteStr:"+student.toByteString());
    }
}
