package com.lxh.seckill.service.ipml;

import com.lxh.seckill.dao.UserMapper;
import com.lxh.seckill.entity.User;
import com.lxh.seckill.param.LoginParam;
import com.lxh.seckill.result.CodeMsg;
import com.lxh.seckill.result.Result;
import com.lxh.seckill.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class UserServiceImplTest {


    @InjectMocks
    @Spy
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login() {
        LoginParam loginParam = Mockito.mock(LoginParam.class);
        Mockito.when(loginParam.getMobile()).thenReturn("18077200000");
        Mockito.when(loginParam.getPassword()).thenReturn("123456");

        //用户为空
        userServiceImpl.login(loginParam);


        User user=new User();
        user.setSalt("9d5b364d");
        user.setPhone("18077200000");

        Mockito.when(userMapper.checkPhone(loginParam.getMobile())).thenReturn(user);

        MockedStatic<MD5Util> md5UtilMockedStatic = Mockito.mockStatic(MD5Util.class);
        //密码错误
        md5UtilMockedStatic.when(()->MD5Util.formPassToDBPass(loginParam.getPassword(),user.getSalt())).thenReturn("321");
        userServiceImpl.login(loginParam);
        Assert.assertNotEquals("ae2fe40a6242ef07a35a30da2232e10a",MD5Util.formPassToDBPass(loginParam.getPassword(),user.getSalt()));

        //密码正确
        md5UtilMockedStatic.when(()->MD5Util.formPassToDBPass(loginParam.getPassword(),user.getSalt())).thenCallRealMethod();
        Result<User> userResult=userServiceImpl.login(loginParam);
        Assertions.assertEquals("18077200000",userResult.getData().getPhone());

    }

    @Test
    void selectAll() {
        when(userMapper.selectAll()).thenReturn(new ArrayList<>());
        userServiceImpl.selectAll();
        Assert.assertEquals(userServiceImpl.selectAll(),userMapper.selectAll());
    }
}