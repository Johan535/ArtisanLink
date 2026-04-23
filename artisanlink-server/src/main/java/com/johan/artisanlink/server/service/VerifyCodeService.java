package com.johan.artisanlink.server.service;

import com.johan.artisanlink.pojo.vo.CaptchaVO;

/**
 * 驗證碼服務
 */
public interface VerifyCodeService {

    CaptchaVO generateAdminCaptcha();

    void validateAdminCaptcha(String key, String code);

    String sendSmsCode(String phone);

    void validateSmsCode(String phone, String code);
}

