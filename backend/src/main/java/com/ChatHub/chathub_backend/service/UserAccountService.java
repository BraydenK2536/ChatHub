package com.ChatHub.chathub_backend.service;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import com.ChatHub.chathub_backend.repository.UserAccountRepository;
import com.ChatHub.chathub_backend.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    private UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccountEntity register(RegisterRequest registerRequest) {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(registerRequest.getUsername());
        return null;
    }
}
