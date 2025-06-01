package com.ChatHub.chathub_backend.controller;

import com.ChatHub.chathub_backend.entity.UserAccountEntity;
import com.ChatHub.chathub_backend.message.SystemMessage;
import com.ChatHub.chathub_backend.request.LoginRequest;
import com.ChatHub.chathub_backend.request.RegisterRequest;
import com.ChatHub.chathub_backend.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController // 声明这是一个 REST 控制器，所有方法默认返回 @ResponseBody
@RequestMapping("/api/auth") // 所有此控制器下的端点都以 /api/auth 开头
public class AuthController {

    private final UserAccountService userAccountService;

    @Autowired
    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        // @RequestBody 注解表示请求体中的 JSON 将被自动转换为 RegisterRequest 对象
        try {
            Optional<UserAccountEntity> loginUser = userAccountService.login(loginRequest);
            if (loginUser.isPresent()) {
                UserAccountEntity userAccountEntity = loginUser.get();
                return ResponseEntity.ok(new SystemMessage(userAccountEntity.getUsername(), "登录成功"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SystemMessage("用户名或密码错误！"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SystemMessage("用户名或密码错误！"));
        }
    }

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            Optional<UserAccountEntity> registerUser = userAccountService.register(registerRequest);
            if (registerUser.isPresent()) {
                UserAccountEntity userAccountEntity = registerUser.get();
                return ResponseEntity.ok(new SystemMessage(userAccountEntity.getUsername(), "注册成功"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SystemMessage("用户名已经被注册！"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SystemMessage("用户名已经被注册！"));
        }
    }
}
