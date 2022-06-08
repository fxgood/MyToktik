package com.njust.service.impl;

import com.njust.bo.UpdatedUserBO;
import com.njust.enums.Sex;
import com.njust.enums.UserInfoModifyType;
import com.njust.enums.YesOrNo;
import com.njust.exceptions.GraceException;
import com.njust.grace.result.ResponseStatusEnum;
import com.njust.mapper.UsersMapper;
import com.njust.pojo.Users;
import com.njust.service.UserService;
import com.njust.utils.DateUtil;
import com.njust.utils.DesensitizationUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    //头像:阿里云oss存的皮卡丘
    private static final String USER_FACE1 = "https://yfx-blog-image.oss-cn-hangzhou.aliyuncs.com/img/pikaqiu.jpg";

    @Override
    public Users queryMobileIsExist(String mobile) {
        Example userExample=new Example(Users.class);
        Example.Criteria criteria=userExample.createCriteria();
        criteria.andEqualTo("mobile",mobile);
        Users user=usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional  //涉及到添加数据,因此开启事务,默认事务隔离级别是可重复读
    @Override
    public Users createUser(String mobile) {
        //获得全局唯一主键
        String userId=sid.nextShort();

        Users user=new Users();
        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户：" + DesensitizationUtil.commonDisplay(mobile));    //DesensitizationUtil脱敏工具
        user.setImoocNum("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE1);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type); //初次可以修改

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }

    @Override
    public Users getUserById(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }

    @Transactional  //涉及到修改,添加事务
    @Override
    public Users updateUserInfo(UpdatedUserBO updatedUserBO) {
        Users pendingUser=new Users();
        BeanUtils.copyProperties(updatedUserBO,pendingUser);
        //selective:若pendingUser某字段为null,则不去覆盖数据库内容
        int res=usersMapper.updateByPrimaryKeySelective(pendingUser);
        if(res!=1){
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
        return getUserById(updatedUserBO.getId());
    }

    @Override
    public Users updateUserInfo(UpdatedUserBO updatedUserBO, Integer type) {
        Example example=new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        if(Objects.equals(type, UserInfoModifyType.NICKNAME.type)){
            criteria.andEqualTo("nickname",updatedUserBO.getNickname());
            Users user = usersMapper.selectOneByExample(example);
            if(user!=null)  //抛出异常,用户名已存在
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
        }
        else if(Objects.equals(type, UserInfoModifyType.IMOOCNUM.type)){
            criteria.andEqualTo("imoocNum",updatedUserBO.getImoocNum());
            Users user = usersMapper.selectOneByExample(example);
            if(user!=null)
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR);
            Users tmpUser=getUserById(updatedUserBO.getId());
            if(Objects.equals(tmpUser.getCanImoocNumBeUpdated(), YesOrNo.NO.type)){
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }
            updatedUserBO.setCanImoocNumBeUpdated(YesOrNo.NO.type); //只能修改一次,修改过后就不能再修改
        }
        return updateUserInfo(updatedUserBO);
    }
}
