package com.johan.artisanlink.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 驗證碼返回 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    /**
     * 開發環境下直接返回驗證碼，便於聯調
     */
    private String code;
}

