package com.njust.service;


import com.njust.bo.UpdatedUserBO;
import com.njust.pojo.Users;

public interface UserService {
    /**
     * 判断用户是否存在,如果存在则返回用户信息
     * @param mobile
     * @return
     */
    public Users queryMobileIsExist(String mobile);

    /**
     * 根据手机号码创建用户
     * @param mobile
     * @return
     */
    public Users createUser(String mobile);


    /**
     * 根据用户主键获取用户
     * @param userId
     * @return
     */
    public Users getUserById(String userId);


    /**
     * 修改用户信息,并返回修改后的User
     * @param updatedUserBO
     * @return
     */
    Users updateUserInfo(UpdatedUserBO updatedUserBO);

    /**
     * 修改某一项用户信息,并返回修改后的User
     * @param updatedUserBO
     * @param type
     * @return
     */
    Users updateUserInfo(UpdatedUserBO updatedUserBO,Integer type);

}
