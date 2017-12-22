package com.sample.service;

import com.sample.DAO.UserRepository;
import com.sample.bean.User;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class LoginService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    //登录
    public Integer login(String mail,String password) {
        if(!checkEmail(mail)) return -3; //邮箱格式错误
        List<User> userList = userRepository.findByMail(mail);
        System.out.println(userList.size());
        if(userList.size()==0) return -2; //邮箱未注册
//        if(userList.size()>1) return -1; //邮箱重复注册
        User user=userList.get(0);
        if(user.getActive()==0) return -1; //邮箱尚未被激活
        if(user.getPassword().equals(password)) return 1; //登录成功
        else return 0; //密码错误
    }

    //注册
    public Integer register(String username, String password,String mail) {
        if(!checkEmail(mail)) return -2; //邮箱格式有问题
        if(!checkPassword(password)) return -1;  //密码长度不在6到20

        List<User> userListByMail=userRepository.findByMail(mail);
        if(userListByMail.size()>0) return 0;//邮箱已被注册

        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setMail(mail);
        user.setActive(0);
        String token = getTokenOfSignUp(user);
        user.setToken(token);
        userRepository.save(user);

        sendEmail(user.getId(),mail,token);
        return 1;
    }

    //激活
    public Integer validate(Integer id, String token) {

        User user=userRepository.findOne(id);
        if(user==null) return -1; //用户不存在

        String userToken = user.getToken();
        if(userToken.equals(token)){
            user.setActive(1);
            userRepository.save(user);
            return 1;//激活成功
        }
        else {
            return 0;//激活失败
        }
    }

    //忘记密码
    public Integer reset(String mail) {
        List<User> userListByMail=userRepository.findByMail(mail);
        if(userListByMail.size()==0) return -1; //邮箱未被注册

        User user=new User();
        user=userListByMail.get(0);

        String password=createPassWord((int)(Math.random()*100),10);
        sendEmailForPassword(mail,password);
        user.setPassword(password);
        userRepository.save(user);
        return 1; //邮件已发送
    }

    public void sendEmail(Integer id,String mail,String token)
    {
        try
        {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("Hey_Net@126.com");
            message.setTo(mail);
            message.setSubject("邮箱验证");

            String link = "http://localhost:8080/#/validate/" + id.toString()+'/'+token;
            message.setText(link);
            this.mailSender.send(mimeMessage);
            System.out.println("bang!");
        }
        catch(Exception ex)
        {
            System.out.println("wrong!");
            System.out.println(token);
        }
    }

    public void sendEmailForPassword(String mail,String password)
    {
        try
        {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("Hey_Net@126.com");
            message.setTo(mail);
            message.setSubject("找回密码");

            String text = "重置密码为"+password;
            message.setText(text);
            this.mailSender.send(mimeMessage);
            System.out.println("bang!");
        }
        catch(Exception ex)
        {
            System.out.println("wrong!");
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
