package com.bbblog.service;

import com.bbblog.entity.Comment;
import com.bbblog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImp implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.getOne(id);
    }

    @Override
    public void removeComment(Long id) {
        commentRepository.deleteById(id);

    }
}
