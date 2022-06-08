package com.njust.mapper;

import com.njust.my.mapper.MyMapper;
import com.njust.pojo.Users;
import org.springframework.stereotype.Repository;

@Repository //数据层注解
public interface UsersMapper extends MyMapper<Users> {

}