package com.ChatHub.chathub_backend.test;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import com.ChatHub.chathub_backend.repository.UserAccountRepository;
import com.ChatHub.chathub_backend.request.LoginRequest;
import com.ChatHub.chathub_backend.request.RegisterRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
public class UserAccountServiceTest {
//
//    private UserAccountRepository userAccountRepository;
//
//    @Autowired
//    public UserAccountServiceTest(UserAccountRepository userAccountRepository) {
//        this.userAccountRepository = userAccountRepository;
//    }
//
//    @PostConstruct
//    public void runTests() throws Exception {
////        try {
////            register(new RegisterRequest("fdgdfgfaaaeee", "eeff"));
////        } catch (Exception e) {
////            System.out.println("Error: " + e.getMessage());
////        }
//
//        try {
//            login(new LoginRequest("fftest","test"));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public Optional<UserAccountEntity> register(RegisterRequest registerRequest) throws Exception {
//        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(registerRequest.getUsername());
//        if (userAccountEntity != null) {
//            //用户名已存在
//            throw new Exception("用户名" + userAccountEntity.getUsername() + "已被注册");
//        } else {
//            UserAccountEntity newUserAccountEntity = new UserAccountEntity(registerRequest.getUsername(), registerRequest.getPassword());
//            userAccountRepository.save(new UserAccountEntity(registerRequest.getUsername(), registerRequest.getPassword()));
//            System.out.println(newUserAccountEntity.getRegisterTime());
//            return Optional.of(newUserAccountEntity);
//        }
//    }
//
//    public Optional<UserAccountEntity> login(LoginRequest loginRequest) throws Exception {
//        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(loginRequest.getUsername());
//        if (userAccountEntity == null) {
//            //账号不存在
//            throw new Exception("账号不存在");
//        }
//        //校验密码是否正确
//        if (userAccountEntity.getPassword().equals(loginRequest.getPassword())) {
//            return Optional.of(userAccountEntity);
//        }else{
//            throw new Exception("密码错误");
//        }
//    }
}