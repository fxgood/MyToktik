package com.njust.controller;


import com.njust.base.BaseInfoProperties;
import com.njust.grace.result.GraceJSONResult;
import com.njust.grace.result.ResponseStatusEnum;
import com.njust.service.FansService;
import com.njust.service.UserService;
import com.njust.utils.PagedGridResult;
import com.njust.vo.VlogerVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("fans")
@Api(tags = "粉丝业务模块")
public class FansController extends BaseInfoProperties {
    @Autowired
    private FansService fansService;

    @Autowired
    private UserService userService;


    @PostMapping("follow")
    public GraceJSONResult follow(@RequestParam String myId,
                                  @RequestParam String vlogerId) {
        //检查参数合法性
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }
        //自己不能关注自己
        if (myId.equalsIgnoreCase(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }
        //查询两个用户是否存在
        if (userService.getUserById(myId) == null || userService.getUserById(vlogerId) == null)
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        fansService.createFollow(myId, vlogerId);
        /** 数据库隔离级别及对应的问题
         * 1. 读未提交  存在 脏读,不可重复读,幻读的问题
         * 2. 读已提交  存在 不可重复读,幻读的问题
         * 3. 可重复度 存在 幻读
         * 4. 串行化
         *
         * 脏读: 读了别人回滚的数据
         * 不可重复读: 两次读数据的中途,有人修改了数据(增删改)
         * 可重复读: 为了保证可重复读,实际上看到的是隔离了数据库真实数据的视图,故读到的情况和数据库真实情况有出入,导致幻读
         * **/
        //打印关注的结果
        log.info("添加了[{}]对[{}]的关注", userService.getUserById(myId).getNickname(), userService.getUserById(vlogerId).getNickname());

        return GraceJSONResult.ok();
    }

    @PostMapping("cancel")
    public GraceJSONResult cancel(@RequestParam String myId,
                                  @RequestParam String vlogerId) {
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)
                || myId.equalsIgnoreCase(vlogerId))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        fansService.cancelFollow(myId, vlogerId);
        log.info("取消了[{}]对[{}]的关注", userService.getUserById(myId).getNickname(), userService.getUserById(vlogerId).getNickname());
        return GraceJSONResult.ok();
    }

    @GetMapping("queryDoIFollowVloger")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam String myId,
                                                @RequestParam String vlogerId) {
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)
                || myId.equalsIgnoreCase(vlogerId))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        boolean doIFollowVloger = fansService.isFollow(myId, vlogerId);
        log.info("查询用户[{}]是否关注了用户[{}]的结果:{}", userService.getUserById(myId).getNickname(), userService.getUserById(vlogerId).getNickname(), doIFollowVloger);
        return GraceJSONResult.ok(doIFollowVloger);
    }

    //我的关注列表
    @GetMapping("queryMyFollows")
    public GraceJSONResult queryMyFollows(@RequestParam String myId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize) {
        PagedGridResult res = fansService.queryMyFollows(myId, page, pageSize);
        return GraceJSONResult.ok(res);
    }

    //我的粉丝列表
    @GetMapping("queryMyFans")
    public GraceJSONResult queryMyFans(@RequestParam String myId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize){
        return GraceJSONResult.ok(fansService.queryMyFans(myId,page,pageSize));
    }
}
