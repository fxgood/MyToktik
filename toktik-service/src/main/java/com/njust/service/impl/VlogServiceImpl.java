package com.njust.service.impl;

import com.github.pagehelper.PageHelper;
import com.njust.base.BaseInfoProperties;
import com.njust.bo.VlogBO;
import com.njust.enums.YesOrNo;
import com.njust.mapper.VlogMapper;
import com.njust.mapper.VlogMapperCustom;
import com.njust.pojo.Vlog;
import com.njust.service.VlogService;
import com.njust.utils.PagedGridResult;
import com.njust.vo.IndexVlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {


    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private Sid sid;    //雪花算法生成唯一id

    @Transactional
    @Override
    public void createVlog(VlogBO vlogBO) {
        String vid=sid.nextShort();
        Vlog vlog=new Vlog();
        BeanUtils.copyProperties(vlogBO,vlog);
        vlog.setId(vid);
        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNo.NO.type);
        Date now=new Date();
        vlog.setCreatedTime(now);
        vlog.setUpdatedTime(now);
        vlogMapper.insert(vlog);
    }

    @Autowired
    private VlogMapperCustom vlogMapperCustom;

    @Override
    public PagedGridResult getIndexVlogList(String search,
                                            Integer page,
                                            Integer pageSize) {
        PageHelper.startPage(page,pageSize);    //todo 这里是如何实现分页的?
        Map<String,Object> mp=new HashMap<>();
        if(StringUtils.isNotBlank(search))
            mp.put("search",search);
        List<IndexVlogVO> list = vlogMapperCustom.getIndexVlogList(mp);
        return setterPagedGrid(list,page);
    }

    @Override
    public IndexVlogVO getVlogDetailById(String vlogId) {
        Map<String,Object>mp=new HashMap<>();
        mp.put("vlogId",vlogId);
        List<IndexVlogVO> list = vlogMapperCustom.getVlogDetailById(mp);
        if(list!=null && list.size()>0) {
            IndexVlogVO vlogVO = list.get(0);
            return vlogVO;
        }
        return null;
    }

    @Transactional
    @Override
    public void changeToPrivateOrPublic(String userId, String vlogId, Integer yesOrNo) {

        Example example=new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",vlogId);
        criteria.andEqualTo("vlogerId",userId);
        Vlog pendingVlog=new Vlog();
        pendingVlog.setIsPrivate(yesOrNo);
        vlogMapper.updateByExampleSelective(pendingVlog/*更新的内容*/,example/*更新的目标行*/);
    }

    @Override
    public PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize, Integer yesOrNo) {
        Example example=new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId",userId);
        criteria.andEqualTo("isPrivate",yesOrNo);
        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        PageHelper.startPage(page,pageSize);    //分页这块暂时没有整明白
        return setterPagedGrid(vlogs,page);
    }
}
