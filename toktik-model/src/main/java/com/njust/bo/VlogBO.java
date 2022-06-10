package com.njust.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VlogBO {
    private String id;
    private String vlogerId;
    @NotBlank
    private String url;
    private String cover;
    @Length(min = 3,max = 10,message = "视频标题长度须在3~10个字符之间!")
    private String title;
    private Integer width;
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
    private Integer isPrivate;
}