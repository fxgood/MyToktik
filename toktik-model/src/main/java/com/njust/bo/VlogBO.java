package com.njust.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VlogBO {
    private String id;
    @NotBlank(message = "用户不存在")
    private String vlogerId;
    @Pattern(regexp = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")
    private String url;
    @NotBlank(message = "视频封面不能为空")
    private String cover;
    @Length(min = 2,max = 20,message = "视频标题长度须在2~20个字符之间!")
    private String title;
    @Min(100)
    private Integer width;
    @Min(100)
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
    private Integer isPrivate;
}