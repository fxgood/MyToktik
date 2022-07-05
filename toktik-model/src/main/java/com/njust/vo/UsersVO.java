package com.njust.vo;

import com.njust.pojo.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.util.Date;


//啥叫VO,viewObject,就是可以提供给视图层(前端)使用的对象
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsersVO {
    private String id;
    private String mobile;
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
    private Date createdTime;
    private Date updatedTime;

    private String userToken;   //用户token,传递给前端

    private Integer myFollowsCounts;    //我关注的博主的总数
    private Integer myFansCounts;   //我的粉丝的数量
    //private Integer myLikedVlogCounts;  //点赞我的视频的总数
    private Integer totalLikeMeCounts;      //点赞我的总数
}

