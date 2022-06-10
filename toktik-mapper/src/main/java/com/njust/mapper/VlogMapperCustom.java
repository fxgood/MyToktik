package com.njust.mapper;

import com.njust.my.mapper.MyMapper;
import com.njust.pojo.Vlog;
import com.njust.vo.IndexVlogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VlogMapperCustom /*extends MyMapper<Vlog> 手动写sql无需继承通用mapper接口*/{
    public List<IndexVlogVO> getIndexVlogList(@Param("paramMap") Map<String,Object> map);

    public List<IndexVlogVO> getVlogDetailById(@Param("paramMap")Map<String, Object> map);
}