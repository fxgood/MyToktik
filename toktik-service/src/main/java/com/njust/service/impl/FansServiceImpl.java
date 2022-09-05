package com.njust.service.impl;

import com.github.pagehelper.PageHelper;
import com.njust.base.BaseInfoProperties;
import com.njust.enums.YesOrNo;
import com.njust.mapper.FansMapper;
import com.njust.mapper.FansMapperCustom;
import com.njust.pojo.Fans;
import com.njust.service.FansService;
import com.njust.utils.PagedGridResult;
import com.njust.vo.FansVO;
import com.njust.vo.VlogerVO;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FansServiceImpl extends BaseInfoProperties implements FansService {
    @Autowired
    private FansMapper fansMapper;  //由myBatis实现接口

    @Autowired
    private Sid sid;    //生成uuid的


    @Transactional  //涉及到数据库非查询操作
    @Override
    public void createFollow(String userId, String vlogerId) {
        //如果已经关注了,那么就不用操作
        /*if (queryFans(userId, vlogerId) != null)
            return;*/
        if(isFollow(userId,vlogerId))
            return;
        Fans fans = new Fans();
        fans.setId(sid.nextShort());
        fans.setFanId(userId);
        fans.setVlogerId(vlogerId);
        //如果对方也关注我,那么修改互粉状态
        Fans vloger = queryFans(vlogerId, userId);
        if (vloger != null) {
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            vloger.setIsFanFriendOfMine(YesOrNo.YES.type);
            fansMapper.updateByPrimaryKeySelective(vloger);
        }
        fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        fansMapper.insert(fans);
        //修改redis
        redis.increment(REDIS_MY_FOLLOWS_COUNTS+":"+userId,1);
        redis.increment(REDIS_MY_FANS_COUNTS+":"+vlogerId,1);
        redis.set(REDIS_FANS_AND_VLOGGER_RELATIONSHIP+":"+userId+":"+vlogerId,"1");   //a关注了b
    }

    @Override
    public void cancelFollow(String userId, String vlogerId) {
        Fans fan = queryFans(userId, vlogerId);
        if(fan==null)
            return;
        Fans vloggerFan=queryFans(vlogerId,userId);
        if(vloggerFan!=null){
            vloggerFan.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateByPrimaryKeySelective(vloggerFan);
        }
        fansMapper.delete(fan);

        redis.decrement(REDIS_MY_FOLLOWS_COUNTS+":"+userId,1);
        redis.decrement(REDIS_MY_FANS_COUNTS+":"+vlogerId,1);
        redis.del(REDIS_FANS_AND_VLOGGER_RELATIONSHIP+":"+userId+":"+vlogerId);   //取消a关注b
    }


    @Override
    public boolean isFollow(String userId, String vlogerId) {
        //return queryFans(userId, vlogerId) != null;
        //使用redis缓存
        String key=REDIS_FANS_AND_VLOGGER_RELATIONSHIP+":"+userId+":"+vlogerId;
        String value=redis.get(key);
        return value != null;
    }

    @Autowired
    private FansMapperCustom fansMapperCustom;

    @Override
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize) {
        Map<String,Object>mp=new HashMap<>();
        PageHelper.startPage(page,pageSize);
        mp.put("myId",myId);
        List<VlogerVO> list = fansMapperCustom.queryMyFollows(mp);
        return setterPagedGrid(list,page);
    }

    @Override
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        Map<String,Object>mp=new HashMap<>();
        mp.put("myId",myId);
        List<FansVO> list = fansMapperCustom.queryMyFans(mp);
        for(FansVO f:list){
            if(f.getHelp()==1)
                f.setFriend(true);
            System.out.println(f);
        }

        return setterPagedGrid(list,page);
    }

    //查询一条关注记录
    private Fans queryFans(String myId, String followedId) {
/*        //使用redis进行查询
        String res=redis.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP+":"+myId+":"+followedId);
        if(res==null)
            return null;
        Fans fans=new Fans();
        fans.setVlogerId(followedId);
        fans.setFanId(myId);
        fans.setIsFanFriendOfMine(null!=redis.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP+":"+followedId+":"+myId)?1:0);
        return fans;*/
      Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("fanId", myId);
        criteria.andEqualTo("vlogerId", followedId);
        List<Fans> fans = fansMapper.selectByExample(example);
        if (fans == null || fans.isEmpty())
            return null;
        return fans.get(0);
    }
}
