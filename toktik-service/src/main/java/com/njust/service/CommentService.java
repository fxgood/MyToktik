package com.njust.service;

import com.njust.bo.CommentBO;
import com.njust.utils.PagedGridResult;
import com.njust.vo.CommentVO;

public interface CommentService {
    /**
     * 创建评论
     */
    public CommentVO createComment(CommentBO Bo);

    /**
     * 查询vlog下的所有评论
     */
    public PagedGridResult queryVlogComment(String vlogId,String userId,Integer page,Integer pageSize);

    /**
     * 删除评论
     */
    public void deleteComment(String commentUserId,
                              String commentID,
                              String vlogId);

    /**
     * 喜欢某评论
     */
    void likeComment(String userId,String commentId);

    /**
     * 不喜欢某评论
     */
    void unlikeComment(String userId,String commentId);
}
