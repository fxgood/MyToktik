package com.njust.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

//被修改的用户信息
//为什么不直接用users,因为有些信息不能被修改如mobile
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdatedUserBO {
    private String id;
    private String nickname;
    private String imoocNum;
    private String face;
    private Integer sex;
    private Date birthday;
    private String country;
    private String province;
    private String city;
    private String district;
    private String description;
    private String bgImg;
    private Integer canImoocNumBeUpdated;
}
