package com.njust.mapper;


import com.njust.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapperCustom {
    List<CommentVO>getCommentList(@Param("vlogId") String vlogId);


}
