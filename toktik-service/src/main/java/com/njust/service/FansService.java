package com.njust.service;

import com.njust.utils.PagedGridResult;
import com.njust.vo.VlogerVO;

import java.util.List;

public interface FansService {
    //关注
    public void createFollow(String userId,String vlogerId);

    //取消关注
    public void cancelFollow(String userId,String vlogerId);

    //查询是否关注
    public boolean isFollow(String userId,String vlogerId);

    //查询粉丝列表
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize);

    //查询关注列表
    public PagedGridResult queryMyFans(String myId,Integer page,Integer pageSize);
    //喜欢

    //查询关注的所有人

    //查询所有粉丝

}
