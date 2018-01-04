package com.sample.service;

import com.sample.DAO.UserRepository;
import com.sample.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EnableAsync
@Service
public class LoginService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private MailService mailService;

    @Value("${spring.mail.username}")
    private String sender;

    //登录
    public Map<String,Object> login(String mail,String password) {
        Map<String,Object> map = new HashMap<>();
        if(!checkEmail(mail)){
            map.put("status","no");
            map.put("error","邮箱格式错误！");
            return map;
        }
        List<User> userList = userRepository.findByMail(mail);
        if(userList.size()==0) {
            map.put("status","no");
            map.put("error","邮箱尚未注册！");
            return map;
        }

        User user=userList.get(0);
        if(user.getActive()==0) {
            map.put("status","no");
            map.put("error","邮箱尚未激活！");
            return map;
        }
        if(user.getPassword().equals(password)) {
            map.put("status","yes");
            map.put("info","登录成功！");
            return map;
        }
        else {
            map.put("status","no");
            map.put("error","密码错误！");
            return map;
        }
    }

    //注册
    public Map<String,Object> register(String username, String password,String mail) {
        Map<String,Object> map = new HashMap<>();
        if(!checkEmail(mail)){
            map.put("status","no");
            map.put("error","邮箱格式错误！");
            return map;
        }
        if(!checkPassword(password)) {
            map.put("status","no");
            map.put("error","密码长度不在6到20！");
            return map;
        }

        List<User> userListByMail=userRepository.findByMail(mail);
        if(userListByMail.size()>0) {
            map.put("status","no");
            map.put("error","邮箱已被注册！");
            return map;
        }

        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setMail(mail);
        user.setActive(0);
        String token = getTokenOfSignUp(user);
        user.setToken(token);
        userRepository.save(user);

        System.out.println("before");
        mailService.sendEmail(user.getId(),mail,token);
        System.out.println("after");
        map.put("status","yes");
        map.put("info","注册成功！");
        return map;
    }

    //激活
    public Map<String,Object> validate(Integer id, String token) {

        Map<String,Object> map = new HashMap<>();
        User user=userRepository.findOne(id);
        if(user==null) {
            map.put("status","no");
            map.put("error","用户不存在！");
            return map;
        }
        String userToken = user.getToken();
        if(userToken.equals(token)){
            user.setActive(1);
            userRepository.save(user);
            map.put("status","yes");
            map.put("info","激活成功！");
            return map;
        }
        else {
            map.put("status","no");
            map.put("error","激活失败！");
            return map;
        }
    }

    //忘记密码
    public Map<String,Object> forget(String mail) {

        Map<String,Object> map = new HashMap<>();
        if(!checkEmail(mail)){
            map.put("status","no");
            map.put("error","邮箱格式错误！");
            return map;
        }
        List<User> userListByMail=userRepository.findByMail(mail);
        if(userListByMail.size()==0) {
            map.put("status","no");
            map.put("error","邮箱未被注册！");
            return map;
        }

        User user=new User();
        user=userListByMail.get(0);

        String password=createPassWord((int)(Math.random()*100),10);
        mailService.sendEmailForPassword(mail,password);
        user.setPassword(password);
        userRepository.save(user);
        map.put("status","yes");
        map.put("info","邮件已发送！");
        return map;
    }

    //修改密码
    public Map<String,Object> reset(String mail,String password,String newpassword) {
        Map<String,Object> map = new HashMap<>();
        map=login(mail,password);
        if(map.get("status").equals("no")){
            return map;
        }
        if(!checkPassword(newpassword)) {
            map.put("status","no");
            map.put("error","密码长度不在6到20！");
            map.remove("info");
            return map;
        }
        else {
            List<User> userList = userRepository.findByMail(mail);
            User user=userList.get(0);
            user.setPassword(newpassword);
            userRepository.save(user);

            map.put("status","yes");
            map.put("info","修改密码成功！");
            return map;
        }

    }




    //生成token
    public String getTokenOfSignUp(User user){
        String token = UUID.randomUUID().toString();
        return token;
    }

    //生成随机密码
    public String createPassWord(int random,int len){
        Random rd = new Random(random);
        final int  maxNum = 62;
        StringBuffer sb = new StringBuffer();
        int rdGet;//取得随机数
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', 'A','B','C','D','E','F','G','H','I','J','K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y' ,'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        int count=0;
        while(count < len){
            rdGet = Math.abs(rd.nextInt(maxNum));//生成的数最大为62-1
            if (rdGet >= 0 && rdGet < str.length) {
                sb.append(str[rdGet]);
                count ++;
            }
        }
        return sb.toString();
    }

    //校验邮箱格式
    boolean checkEmail(String mail){
        //校验邮箱格式
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$");
        Matcher m = p.matcher(mail);
        if(!m.matches()){
            return false;
        }
        else return true;
    }

    //校验密码长度
    boolean checkPassword(String password){
        //校验密码长度
        Pattern p = Pattern.compile("^\\w{6,20}$");
        Matcher m = p.matcher(password);
        if(!m.matches()){
            return false;
        }
        else return true;
    }
}
