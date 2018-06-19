package com.example;


import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CaiXiaoLing on 2018/6/12.
 */
public class test {

    @Test
    public void testdemo(){
//        System.out.println(2216867%4);

        String temp = "using_states\":[[\"已使用数\\/总帐号数: 1\\/20\"]],\"timeout_states";
        System.out.println(temp.replace("\\",""));
        System.out.println(temp.replace("/",""));

//        temp = ${key};
//        temp.replace("\\","");
//        vars.put("newKey",temp);
    }


    @Test
    public void testRegex(){

        String input = "using_states\":[[\"已使用数\\/总帐号数: 1\\/20\"]],\"timeout_states";
//        String regex = "\"已使用数\\/总帐号数: 1\\/(.+?)\"";
//        String regex = "using_states\":(?<=\\[\\[)\"已使用数(.+?)/总帐号数: 1(.+?)/(.+?)\"(?=\\]\\]),\"timeout_states";
        String regex = "using_states\":(?<=\\[\\[)(\\S+)(?=\\]\\]),\"timeout_states";
//        String regex = "\"已使用数(.+?)总帐号数: 1(.+?)20\"";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input); // 获取 matcher 对象

        System.out.println("lookingAt(): "+m.lookingAt());
        System.out.println("matches(): "+m.matches());

        while(m.find()) {
            System.out.println("group1:"+m.group(1)+", group2:"+m.group(2)+", group3:"+m.group(3));
            System.out.println("group4:"+m.group(4)+", group5:"+m.group(5)+", group6:"+m.group(6));
        }

    }

    @Test
    public void testRegex1(){

//        String input = "[[已使用数]]";
        String input = "using_states\":[[\"已使用数\\/总帐号数: 1\\/20\"]],\"timeout_states";
        String regex = "(?<=\\[\\[)\"已使用数([\\s\\S]+)总帐号数: \\d{1,}([\\s\\S]+)\\d{2}\"(?=\\]\\])";
//        String regex = "(?<=using_states\":\\[\\[)\"已使用数\\S{2}总帐号数: \\d{1,}\\S{2}(.+?)\"(?=\\]\\],\"timeout_states)";
//        String regex = "(?<=using_states\":\\[\\[)[\\s\\S]+\\d{1,}\\S{2}(.+?)\"(?=\\]\\],\"timeout_states)";
//        String regex = "(?<=using_states\":\\[\\[)[\\s\\S]+\\d{1,}\\S{2}(\\d{1,})(?=\"\\]\\],\"timeout_states)";
//        String regex = "(?<=using_states\":\\[\\[\"已使用数\\S{2}总帐号数: )\\d{1,}\\S{2}(\\d{1,})(?=\"\\]\\],\"timeout_states)";
//        String regex = "(?<=\\[)(\\S+)(?=\\])";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input); // 获取 matcher 对象

        System.out.println("lookingAt(): "+m.lookingAt());
        System.out.println("matches(): "+m.matches());

        while(m.find()) {
            System.out.println("group1:"+m.group(1)/*+", group2:"+m.group(2)+", group3:"+m.group(3)*/);        }

    }
}
