package com.njust.controller;


import com.njust.MinIOConfig;
import com.njust.base.BaseInfoProperties;
import com.njust.bo.UpdatedUserBO;
import com.njust.enums.FileTypeEnum;
import com.njust.enums.UserInfoModifyType;
import com.njust.grace.result.GraceJSONResult;
import com.njust.grace.result.ResponseStatusEnum;
import com.njust.pojo.Users;
import com.njust.service.UserService;
import com.njust.utils.MinIOUtils;
import com.njust.vo.UsersVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("userInfo")
@Slf4j
@Api(tags = "用户信息接口")
public class UserInfoController extends BaseInfoProperties {

    @Autowired
    private UserService userService;

    @GetMapping("query")    //是否应该改造一下? 你想查就查?不得带个令牌啥的
    public GraceJSONResult query(@RequestParam String userId){
        Users user= userService.getUserById(userId);
        UsersVO usersVO=new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        //redis_my_follows_counts:220607HA68DR0A3C
        //我关注的博主的数量 todo如果为空呢?
        String myFollowCountsStr=redis.get(REDIS_MY_FOLLOWS_COUNTS+":"+userId);
        //我的粉丝总数
        String myFansCountsStr=redis.get(REDIS_MY_FANS_COUNTS+":"+userId);
        //用户获赞总数(视频+评论)
        String likedVlogCountsStr=redis.get(REDIS_VLOG_BE_LIKED_COUNTS+":"+userId);
        String likedVlogerCountsStr=redis.get(REDIS_VLOGER_BE_LIKED_COUNTS+":"+userId);

        Integer myFollowsCounts=0;    //我关注的博主的总数
        Integer myFansCounts=0;   //我的粉丝的数量
        Integer totalLikeMeCounts=0;      //点赞我的总数

        if(StringUtils.isNotBlank(myFollowCountsStr))
            myFollowsCounts=Integer.parseInt(myFollowCountsStr);
        if(StringUtils.isNotBlank(myFansCountsStr))
            myFansCounts=Integer.parseInt(myFansCountsStr);
        if(StringUtils.isNotBlank(likedVlogCountsStr) && StringUtils.isNotBlank(likedVlogerCountsStr))
            totalLikeMeCounts=Integer.parseInt(likedVlogCountsStr)+Integer.parseInt(likedVlogerCountsStr);
        usersVO.setTotalLikeMeCounts(totalLikeMeCounts);
        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setMyFollowsCounts(myFollowsCounts);
        log.info("请求\""+user.getNickname()+"\"用户数据成功");
        return GraceJSONResult.ok(usersVO);

    }

    @PostMapping("modifyUserInfo")
    public GraceJSONResult modifyUserInfo(@RequestParam Integer type,
                                          @RequestBody UpdatedUserBO updatedUserBO){
        UserInfoModifyType.checkUserInfoTypeIsRight(type);  //检查type
        Users userInfo = userService.updateUserInfo(updatedUserBO, type);
        log.info("用户"+userService.getUserById(updatedUserBO.getId()).getNickname()+"更新信息成功");
        return GraceJSONResult.ok(userInfo);    //返回更新后的用户信息
    }

    @Autowired
    private MinIOConfig minIOConfig;

    @PostMapping("modifyImage")
    public GraceJSONResult modifyImage(@RequestParam String userId,
                                      @RequestParam Integer type,
                                      MultipartFile file) throws Exception {
        if(!Objects.equals(FileTypeEnum.BGIMG.type,type) && !Objects.equals(FileTypeEnum.FACE.type,type))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        String fileName=file.getOriginalFilename();

        MinIOUtils.uploadFile(minIOConfig.getBucketName(),fileName,file.getInputStream());

        String imgUrl= minIOConfig.getFileHost()+"/"+minIOConfig.getBucketName()+"/"+fileName;
        //图片地址入库
        UpdatedUserBO updatedUserBO=new UpdatedUserBO();
        updatedUserBO.setId(userId);
        if(type.equals(FileTypeEnum.FACE.type))
            updatedUserBO.setFace(imgUrl);
        else
            updatedUserBO.setBgImg(imgUrl);
        Users users = userService.updateUserInfo(updatedUserBO);
        log.info(type==1?"背景图修改成功":"头像修改成功");
        return GraceJSONResult.ok(users);
    }
}
