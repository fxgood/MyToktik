package com.njust.controller;


import com.njust.base.BaseInfoProperties;
import com.njust.bo.VlogBO;
import com.njust.enums.YesOrNo;
import com.njust.grace.result.GraceJSONResult;
import com.njust.grace.result.ResponseStatusEnum;
import com.njust.pojo.Users;
import com.njust.pojo.Vlog;
import com.njust.service.FansService;
import com.njust.service.UserService;
import com.njust.service.VlogService;
import com.njust.utils.PagedGridResult;
import com.njust.vo.IndexVlogVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "短视频相关接口")
@RequestMapping("vlog")
public class VlogController extends BaseInfoProperties {

    @Autowired
    private VlogService vlogService;

    @GetMapping("myLikedList")
    public GraceJSONResult myLikedList(@RequestParam String userId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize){
        PagedGridResult result = vlogService.mylikedList(userId, page, pageSize);
        return GraceJSONResult.ok(result);
    }
    @PostMapping("totalLikedCounts")
    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId){
        Integer cnt=vlogService.vlogLikedCounts(vlogId);
        return GraceJSONResult.ok(cnt);
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                  @RequestParam String vlogerId,
                                  @RequestParam String vlogId){
        vlogService.unlike(userId,vlogerId,vlogId);
        return GraceJSONResult.ok();
    }

    @Autowired
    private UserService userService;
    //用户点赞视频
    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId){
        //判断参数的合法性
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(vlogerId) || StringUtils.isBlank(vlogerId))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        //查询用户是否存在
        Users user=userService.getUserById(userId);
        Users vloger=userService.getUserById(vlogerId);
        if(user==null || vloger==null)
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        IndexVlogVO vlogDetailById = vlogService.getVlogDetailById(userId,vlogId);
        if(vlogDetailById==null)
            return GraceJSONResult.errorMsg("vlog不存在");
        vlogService.userLikeVlog(userId,vlogerId,vlogId);

        return GraceJSONResult.ok();

    }

    @PostMapping("publish")
    public GraceJSONResult publish(@Valid @RequestBody VlogBO vlogBO) {
        vlogService.createVlog(vlogBO);
        return GraceJSONResult.ok();
    }

    @GetMapping("indexList")
    public GraceJSONResult indexList(@RequestParam String userId,
                                     @RequestParam(defaultValue = "") String search,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) {   //要写默认值,否则knife4j测试强制要求写这个字段
        if (page == null)
            page = COMMON_START_PAGE;
        if (pageSize == null)
            pageSize = COMMON_PAGE_SIZE;
        PagedGridResult gridResult = vlogService.getIndexVlogList(userId,search, page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }

    @GetMapping("detail")
    public GraceJSONResult detail(@RequestParam(defaultValue = "") String userId,
                                  @RequestParam String vlogId) {
        IndexVlogVO detail = vlogService.getVlogDetailById(userId,vlogId);
        return GraceJSONResult.ok(detail);
    }

    @PostMapping("changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                           @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @PostMapping("changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                          @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    @GetMapping("myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize) {
        if (page == null)
            page = COMMON_START_PAGE;
        if (pageSize == null)
            pageSize = COMMON_PAGE_SIZE;
        PagedGridResult gridResult=vlogService.queryMyVlogList(userId,
                                                                page,
                                                                pageSize,
                                                                YesOrNo.NO.type);
        return GraceJSONResult.ok(gridResult);
    }

    @GetMapping("myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize) {
        if (page == null)
            page = COMMON_START_PAGE;
        if (pageSize == null)
            pageSize = COMMON_PAGE_SIZE;
        PagedGridResult gridResult=vlogService.queryMyVlogList(userId,
                page,
                pageSize,
                YesOrNo.YES.type);
        return GraceJSONResult.ok(gridResult);
    }

    @GetMapping("followList")
    public GraceJSONResult followList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize
                                      ){
        PagedGridResult myFollowVlogList = vlogService.getMyFollowVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(myFollowVlogList);
    }

    //获取朋友互相关注的视频
    @GetMapping("friendList")
    public GraceJSONResult friendList(@RequestParam String myId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize){

        PagedGridResult gridResult = vlogService.friendVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }


}
