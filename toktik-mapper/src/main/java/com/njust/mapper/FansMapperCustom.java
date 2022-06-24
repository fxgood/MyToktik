package com.njust.mapper;

import com.njust.my.mapper.MyMapper;
import com.njust.pojo.Fans;
import com.njust.vo.FansVO;
import com.njust.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FansMapperCustom extends MyMapper<Fans> {
    List<VlogerVO>queryMyFollows(@Param("paramMap")Map<String,Object>mp);

    List<FansVO>queryMyFans(@Param("paramMap")Map<String,Object>mp);
}