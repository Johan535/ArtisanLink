package com.ArtisanLink.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffSaveDTO {
    private Long merchantId;
    private String name;
    private String phone;
    private String position;
    private LocalDate entryTime;
    private String[] skillTags;
    private String avatar;
    private String remark;
    private String status;
    private String createTime;
    private String updateTime;
    private String deleteTime;
    private String deleteFlag;
    private String createUser;
    private String updateUser;
    private String deleteUser;
}
