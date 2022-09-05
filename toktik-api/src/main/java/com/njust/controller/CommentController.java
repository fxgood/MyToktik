package com.njust.controller;


import com.njust.base.BaseInfoProperties;
import com.njust.bo.CommentBO;
import com.njust.grace.result.GraceJSONResult;
import com.njust.service.CommentService;
import com.njust.utils.PagedGridResult;
import com.njust.vo.CommentVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "CommentController 评论模块接口")
@RestController
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {
    @Autowired
    private CommentService commentService;  //接口  spring自动注入实现类 先不要type再byname

    /**
     * 创建评论
     * @param bo
     * @return
     */
    @PostMapping("create")
    public GraceJSONResult create(@RequestBody CommentBO bo) {
        CommentVO vo = commentService.createComment(bo);
        return GraceJSONResult.ok(vo);
    }

    /**
     * 返回当前vlog的评论总数
     */
    @GetMapping("counts")
    public GraceJSONResult counts(@RequestParam String vlogId){
        String val=redis.get(REDIS_VLOG_COMMENT_COUNTS+":"+vlogId);
        if(StringUtils.isBlank(val))
            val="0";
        return GraceJSONResult.ok(Integer.valueOf(val));
    }

    /**
     * 查询评论列表
     */
    @GetMapping("list")
    public GraceJSONResult list(@RequestParam String vlogId,
                                @RequestParam String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize){
        PagedGridResult gridResult = commentService.queryVlogComment(vlogId, userId,page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("delete")
    public GraceJSONResult delete(@RequestParam String commentUserId,
                                  @RequestParam String commentId,
                                  @RequestParam String vlogId){
        commentService.deleteComment(commentUserId,commentId,vlogId);
        return GraceJSONResult.ok();
    }

    /**
     * 喜欢某评论
     */
    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String commentId){
        commentService.likeComment(userId,commentId);
        return GraceJSONResult.ok();
    }

    /**
     * 不喜欢评论
     */
    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                  @RequestParam String commentId){
        commentService.unlikeComment(userId,commentId);
        return GraceJSONResult.ok();
    }


}


