package com.johan.artisanlink.server.controller;

import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.pojo.dto.SmsCodeDTO;
import com.johan.artisanlink.pojo.dto.UserLoginDTO;
import com.johan.artisanlink.server.service.UserService;
import com.johan.artisanlink.server.service.VerifyCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * C端登录相关接口（兼容前端/customer前缀）
 */
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final UserService userService;
    private final VerifyCodeService verifyCodeService;

    @PostMapping("/sms/send")
    public Result<Map<String, String>> sendSms(@Valid @RequestBody SmsCodeDTO dto) {
        String code = verifyCodeService.sendSmsCode(dto.getPhone());
        Map<String, String> data = new HashMap<>();
        // 开发联调时返回验证码，便于测试
        data.put("code", code);
        return Result.success("验证码发送成功", data);
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return Result.success("登录成功", data);
    }
}

