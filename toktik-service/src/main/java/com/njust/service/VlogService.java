package com.njust.service;

import com.njust.bo.VlogBO;
import com.njust.utils.PagedGridResult;
import com.njust.vo.IndexVlogVO;

import java.util.List;
import java.util.Map;

public interface VlogService {

    //新增视频
    public void createVlog(VlogBO vlogBO);

    //查询or搜索vlog列表
    public PagedGridResult getIndexVlogList(String search,
                                            Integer page,
                                            Integer pageSize);    //search可以为空,代表首页视频列表;不为空则是搜索页面的视频列表
    //根据视频主键查询vloger详细信息
    public IndexVlogVO getVlogDetailById(String vlogId);
}
