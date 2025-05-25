package com.ChatHub.chathub_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_account") // 指定映射到数据库中的表名
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//唯一标识

    @Column(nullable = false, length = 50, unique = true) // 映射到列，nullable=false 表示不能为空，length 指定字符串长度
    private String username; // 用户名

    @Column(nullable = false, length = 20)
    private String password; // 用户名

    @Column
    private ZonedDateTime registerTime;

    public UserAccountEntity( String username,String password) {
        this.username = username;
        this.password = password;
        this.registerTime = ZonedDateTime.now(ZoneId.systemDefault());
    }

    public UserAccountEntity() {
    }

    public UserAccountEntity(Long id, String username, String password) {
        this.registerTime = ZonedDateTime.now(ZoneOffset.UTC);
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserAccountEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public ZonedDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(ZonedDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
