package com.demo.base;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
class BaseApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    StringEncryptor encryptor;

    @Test
    public void getPass() {
        String url1 = "jdbc:mysql://192.168.20.196:3306/zhypt_base_3.0?useUnicode=true&characterEncoding=UTF8&allowMultiQueries=true";
        String username = "root";
        String password1 = "demo@*demo@*123";
        String url = encryptor.encrypt(url1);
        String name = encryptor.encrypt(username);
        String password = encryptor.encrypt(password1);
        System.out.println(url+"----------------");
        System.out.println(name+"----------------");
        System.out.println(password+"----------------");
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void getOauthPassword() {
        String passwrod = passwordEncoder.encode("888888");
        System.out.println("password==="+passwrod);
    }


    @Test
    public void ts() {
        ArrayList<Object> objects = new ArrayList<>();
        System.out.printf((null instanceof List)+"");
    }




}
