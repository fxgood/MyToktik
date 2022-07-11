package com.njust.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


//查询到的短视频的信息,返回给前端用
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IndexVlogVO {
    //博主的信息
    private String vlogerId;
    private String vlogerFace;
    private String vlogerName;
    //视频信息
    private String vlogId;
    private String content;
    private String url;
    private String cover;
    private Integer width;
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
    private Integer isPrivate;
    private boolean isPlay = false;
    private boolean doIFollowVloger = false;
    private boolean doILikeThisVlog = false;
}