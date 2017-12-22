package com.sample.controller;
import com.sample.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import net.sf.json.JSONObject;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;


    //登录
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Integer login(@RequestBody JSONObject jsonObject){
        String mail=jsonObject.getString("mail");
        String password=jsonObject.getString("password");
        return loginService.login(mail,password);
    }


    //注册
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public Integer register(@RequestBody JSONObject jsonObject){
        String username=jsonObject.getString("username");
        String password=jsonObject.getString("password");
        String mail = jsonObject.getString("mail");
        return loginService.register(username,password,mail);
    }

    //激活
    @RequestMapping(value="/validate/{id}/{token}",method = RequestMethod.POST)
    public Integer validate(@PathVariable ("id") Integer id,@PathVariable("token") String token){
        return loginService.validate(id,token);
    }

    //忘记密码
    @RequestMapping(value="/forgetpassword",method = RequestMethod.POST)
    public Integer reset(@RequestParam ("mail") String mail){
        return loginService.reset(mail);
    }

}


