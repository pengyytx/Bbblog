package com.bbblog.service;

import com.bbblog.entity.Blog;
import com.bbblog.entity.Catalog;
import com.bbblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    Blog saveBlog(Blog blog);

    void removeBlog(Long id);

    Blog getBlogById(Long id);

    //根据用户进行博客名称分页模糊查询（最新）
    Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable);

    //根据用户进行博客名称分页模糊查询（最新）
    Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable);

    //阅读量递增
    void readingIncrease(Long id);

    Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable);

    Blog createComment(Long blogId, String commentContent);

    void removeComment(Long blogId, Long commentId);

    Blog createVote(Long blogId);

    void removeVote(Long blogId, Long voteId);
}
