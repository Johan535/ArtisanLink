package com.johan.artisanlink.server.service.impl;

import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.pojo.vo.CaptchaVO;
import com.johan.artisanlink.server.service.VerifyCodeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 簡易驗證碼服務（內存版，滿足開發聯調）
 */
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private static final long EXPIRE_SECONDS = 300;

    private static class CodeEntry {
        private final String code;
        private final LocalDateTime expireTime;

        private CodeEntry(String code, LocalDateTime expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }

    private final Map<String, CodeEntry> adminCaptchaStore = new ConcurrentHashMap<>();
    private final Map<String, CodeEntry> smsCodeStore = new ConcurrentHashMap<>();

    @Override
    public CaptchaVO generateAdminCaptcha() {
        String key = UUID.randomUUID().toString();
        String code = randomDigits(4);
        adminCaptchaStore.put(key, new CodeEntry(code, LocalDateTime.now().plusSeconds(EXPIRE_SECONDS)));
        return new CaptchaVO(key, code);
    }

    @Override
    public void validateAdminCaptcha(String key, String code) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(code)) {
            throw new BusinessException("验证码不能为空");
        }
        CodeEntry entry = adminCaptchaStore.get(key);
        if (entry == null || LocalDateTime.now().isAfter(entry.expireTime)) {
            adminCaptchaStore.remove(key);
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!entry.code.equalsIgnoreCase(code.trim())) {
            throw new BusinessException("验证码错误");
        }
        adminCaptchaStore.remove(key);
    }

    @Override
    public String sendSmsCode(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        String code = randomDigits(6);
        smsCodeStore.put(phone, new CodeEntry(code, LocalDateTime.now().plusSeconds(EXPIRE_SECONDS)));
        return code;
    }

    @Override
    public void validateSmsCode(String phone, String code) {
        if (!StringUtils.hasText(phone) || !StringUtils.hasText(code)) {
            throw new BusinessException("手机号或验证码不能为空");
        }
        CodeEntry entry = smsCodeStore.get(phone);
        if (entry == null || LocalDateTime.now().isAfter(entry.expireTime)) {
            smsCodeStore.remove(phone);
            throw new BusinessException("短信验证码已过期，请重新获取");
        }
        if (!entry.code.equals(code.trim())) {
            throw new BusinessException("短信验证码错误");
        }
        smsCodeStore.remove(phone);
    }

    private String randomDigits(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }
}

