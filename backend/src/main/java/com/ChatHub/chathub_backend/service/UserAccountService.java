package com.ChatHub.chathub_backend.service;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import com.ChatHub.chathub_backend.repository.UserAccountRepository;
import com.ChatHub.chathub_backend.request.LoginRequest;
import com.ChatHub.chathub_backend.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountService {

    private UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public Optional<UserAccountEntity> register(RegisterRequest registerRequest) throws Exception {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(registerRequest.getUsername());
        if (userAccountEntity != null) {
            //用户名已存在
            throw new Exception("用户名" + userAccountEntity.getUsername() + "已被注册");
        } else {
            UserAccountEntity newUserAccountEntity = new UserAccountEntity(registerRequest.getUsername(), registerRequest.getPassword());
            userAccountRepository.save(new UserAccountEntity(registerRequest.getUsername(), registerRequest.getPassword()));
            System.out.println(newUserAccountEntity.getRegisterTime());
            return Optional.of(newUserAccountEntity);
        }
    }

    public Optional<UserAccountEntity> login(LoginRequest loginRequest) throws Exception {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(loginRequest.getUsername());
        if (userAccountEntity == null) {
            //账号不存在
            throw new Exception("账号不存在");
        }
        //校验密码是否正确
        if (userAccountEntity.getPassword().equals(loginRequest.getPassword())) {
            return Optional.of(userAccountEntity);
        }else{
            throw new Exception("密码错误");
        }
    }
}
