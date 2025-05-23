package com.ChatHub.chathub_backend.test;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import com.ChatHub.chathub_backend.repository.UserAccountRepository;
import com.ChatHub.chathub_backend.request.RegisterRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountServiceTest {

    private UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountServiceTest(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @PostConstruct
    public void runTests() throws Exception {
        register(new RegisterRequest("aaaeqqq","eeff"));
    }

    public Optional<UserAccountEntity> register(RegisterRequest registerRequest) throws Exception {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(registerRequest.getUsername());
        System.out.println("================================UserAccountServiceTest=======================================");
        if (userAccountEntity != null) {
            //用户名已存在
            throw new Exception("用户名" + userAccountEntity.getUsername() + "已被注册");
        }else{
            UserAccountEntity newUserAccountEntity = new UserAccountEntity(registerRequest.getUsername(), registerRequest.getPassword());
            userAccountRepository.save(new UserAccountEntity(registerRequest.getUsername(), registerRequest.getPassword()));
            System.out.println(newUserAccountEntity.getRegisterTime());
            System.out.println("注册成功");
            System.out.println("=======================================================================");

            return Optional.of(newUserAccountEntity);
        }
    }
}