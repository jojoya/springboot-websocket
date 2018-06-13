package com.example.demo.controller;

import com.example.demo.service.SocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by CaiXiaoLing on 2018/6/8.
 */
@Controller
public class IndexController {

    @RequestMapping("/userclient")
    @ResponseBody
    public String userclient(){
        return "userclient";
    }

    @RequestMapping("/tongji")
    @ResponseBody
    public String tongji(Model model){
        model.addAttribute("num", SocketServer.getOnlineNum());
        model.addAttribute("users", SocketServer.getOnlineUsers());
        return "tongji";
    }


    @RequestMapping("/sendmsg")
    @ResponseBody
    public String sendmsg(HttpServletRequest request){
        String username = request.getParameter("username");
        String msg = request.getParameter("msg");
        System.out.println("@RequestMapping(\"/sendmsg\"),msg:"+msg + "username:"+username);
        SocketServer.sendMessage(msg,username);
        return "success";
    }


    @RequestMapping("/sendAll")
    @ResponseBody
    public String sendAll(HttpServletRequest request){
        String msg = request.getParameter("msg");
        System.out.println("@RequestMapping(\"/sendAll\"),msg:"+msg);
        SocketServer.sendAll(msg);
        return "success";
    }



}
