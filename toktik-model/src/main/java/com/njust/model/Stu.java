package com.njust.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data   //lombok帮我们生成了getter setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Stu {
    private String name;
    private Integer age;
}

