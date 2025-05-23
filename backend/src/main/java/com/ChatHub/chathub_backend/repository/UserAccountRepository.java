package com.ChatHub.chathub_backend.repository;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountEntity,Long> {

}
