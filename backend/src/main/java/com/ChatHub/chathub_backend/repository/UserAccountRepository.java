package com.ChatHub.chathub_backend.repository;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountEntity,Long> {
    //用户信息数据库的接口
    UserAccountEntity findByUsername(String username);
}
