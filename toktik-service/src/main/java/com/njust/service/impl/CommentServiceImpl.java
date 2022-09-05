package com.njust.service.impl;

import com.github.pagehelper.PageHelper;
import com.njust.base.BaseInfoProperties;
import com.njust.bo.CommentBO;
import com.njust.mapper.CommentMapper;
import com.njust.mapper.CommentMapperCustom;
import com.njust.pojo.Comment;
import com.njust.service.CommentService;
import com.njust.utils.PagedGridResult;
import com.njust.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {

    @Autowired
    private Sid sid;

    @Autowired
    private CommentMapper commentMapper;   //CommentMapper是一个接口

    @Override
    public CommentVO createComment(CommentBO bo) {
        Comment comment = new Comment();
        comment.setId(sid.nextShort());
        comment.setCommentUserId(bo.getCommentUserId());
        comment.setLikeCounts(0);
        comment.setVlogerId(bo.getVlogerId());
        comment.setVlogId(bo.getVlogId());
        comment.setContent(bo.getContent());
        comment.setCreateTime(new Date());
        comment.setFatherCommentId(bo.getFatherCommentId());
        //存入数据库
        commentMapper.insert(comment);
        //修改redis数据,增加视频评论数
        redis.increment(REDIS_VLOG_COMMENT_COUNTS+":"+comment.getVlogId(),1);
        CommentVO vo=new CommentVO();
        BeanUtils.copyProperties(comment,vo);
        return vo;
    }

    @Autowired
    private CommentMapperCustom commentMapperCustom;

    @Override
    public PagedGridResult queryVlogComment(String vlogId,String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<CommentVO> commentList = commentMapperCustom.getCommentList(vlogId);
        for(CommentVO c:commentList){
            //评论的点赞数
            int cnt=0;
            String cntStr=redis.get(REDIS_VLOG_COMMENT_LIKED_COUNTS+":"+c.getCommentId());
            if(StringUtils.isNotBlank(cntStr))
                cnt=Integer.parseInt(cntStr);
            c.setLikeCounts(cnt);
            //评论是否被当前用户所喜爱
            c.setIsLike(redis.keyIsExist(REDIS_USER_LIKE_COMMENT+":"+userId+":"+c.getCommentId())?1:0);
        }
        return setterPagedGrid(commentList,page);
    }

    @Override
    public void deleteComment(String commentUserId, String commentID, String vlogId) {
        //查询comment所属用户id是否和请求id相同,如果不同,报错
        Comment pendingDelete=new Comment();
        pendingDelete.setId(commentID);
        pendingDelete.setCommentUserId(commentUserId);
        commentMapper.delete(pendingDelete);//必须满足上面两个条件相等,才会删除
        //vlog评论总数-1
        String cntStr=redis.get(REDIS_VLOG_COMMENT_COUNTS+":"+vlogId);
        if(cntStr!=null && Integer.parseInt(cntStr)>0)
            redis.decrement(REDIS_VLOG_COMMENT_COUNTS+":"+vlogId,1);
    }

    @Override
    public void likeComment(String userId, String commentId) {
        //标记用户喜欢该评论
        redis.set(REDIS_USER_LIKE_COMMENT+":"+userId+":"+commentId,"1");
        //增加该评论点赞数量
        redis.increment(REDIS_VLOG_COMMENT_LIKED_COUNTS+":"+commentId,1);
    }

    @Override
    public void unlikeComment(String userId, String commentId) {
        redis.del(REDIS_USER_LIKE_COMMENT+":"+userId+":"+commentId);
        String cnt=redis.get(REDIS_VLOG_COMMENT_LIKED_COUNTS+":"+commentId);
        int c=0;
        if(StringUtils.isNotBlank(cnt))
            c=Integer.parseInt(cnt);
        if(c>0)
            redis.decrement(REDIS_VLOG_COMMENT_LIKED_COUNTS+":"+commentId,1);
    }
}





