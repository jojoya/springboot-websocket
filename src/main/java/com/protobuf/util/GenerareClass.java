package com.protobuf.util;

import java.io.IOException;

/**
 * 通过执行cmd命令调用protoc.exe程序
 * */
public class GenerareClass {
    public static void main(String[] args) {
        String protoFile = "test.proto";
        String strCmd = "./src/proto/protoc.exe  --java_out=./src/main/java ./src/proto/"+ protoFile;

        try {
            Runtime.getRuntime().exec(strCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
