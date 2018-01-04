package com.sample.controller;
import com.sample.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;


    //登录
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Map<String,Object> login(@RequestBody JSONObject jsonObject){
        try{
            String email=jsonObject.getString("email");
            String password=jsonObject.getString("password");
            System.out.println("login email: "+email+"-password: "+password);
            return loginService.login(email,password);
        }
        catch(Exception e){
            System.out.println("登录失败！");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("status","no");
        map.put("error","登录失败！");
        return map;
    }


    //注册
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public Map<String,Object> register(@RequestBody JSONObject jsonObject){
        try{
            String username=jsonObject.getString("username");
            String password=jsonObject.getString("password");
            String email = jsonObject.getString("email");
            System.out.println("register email: "+email+"-password: "+password+"-username: "+username);
            return loginService.register(username,password,email);
        }
        catch(Exception e){
            System.out.println("注册失败！");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("status","no");
        map.put("error","注册失败！");
        return map;
    }


    //激活
    @RequestMapping(value="/validate",method = RequestMethod.POST)
    public Map<String,Object> validate(@RequestBody JSONObject jsonObject){
        try{
            String userID=jsonObject.getString("userID");
            String token=jsonObject.getString("token");
            Integer id = Integer.parseInt(userID);
            return loginService.validate(id,token);
        }
        catch(Exception e){
            System.out.println("激活失败：字符串转换为整型失败");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("status","no");
        map.put("error","激活失败！");
        return map;
    }

    //忘记密码
    @RequestMapping(value="/forgetpassword",method = RequestMethod.POST)
    public Map<String,Object> forget(@RequestBody JSONObject jsonObject){
        try{
            String email=jsonObject.getString("email");
            return loginService.forget(email);
        }
        catch(Exception e){
            System.out.println("找回密码失败！");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("status","no");
        map.put("error","找回密码失败！");
        return map;
    }

    //修改密码
    @RequestMapping(value="/resetpassword",method = RequestMethod.POST)
    public Map<String,Object> reset(@RequestBody JSONObject jsonObject){
        try{
            String email=jsonObject.getString("email");
            String password=jsonObject.getString("password");
            String newpassword=jsonObject.getString("newpassword");
            return loginService.reset(email,password,newpassword);
        }
        catch(Exception e){
            System.out.println("修改密码失败！");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("status","no");
        map.put("error","修改密码失败！");
        return map;
    }

}


