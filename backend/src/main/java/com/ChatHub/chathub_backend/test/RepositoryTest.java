package com.ChatHub.chathub_backend.test;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import com.ChatHub.chathub_backend.repository.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

//@Service
public class RepositoryTest {
    private final UserAccountRepository userAccountRepository;

//    @Autowired
    public RepositoryTest(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @PostConstruct
    public void runTests() {
        findUserByIdAndPrint(1L);
    }

    public void findUserByIdAndPrint(Long userId) {
        Optional<UserAccountEntity> user = userAccountRepository.findById(userId);
        user.ifPresent(System.out::println);

    }
}
