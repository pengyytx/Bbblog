package com.bbblog.service;

import com.bbblog.entity.Comment;

public interface CommentService {
    Comment getCommentById(Long id);

    void removeComment(Long id);
}
