package com.bbblog.service;

import com.bbblog.entity.*;
import com.bbblog.entity.es.EsBlog;
import com.bbblog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BlogServiceImp implements BlogService {
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private EsBlogService esBlogService;

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        boolean isNew = (blog.getId() == null);
        EsBlog esBlog;

        Blog returnBlog = blogRepository.save(blog);

        if (isNew) {
            esBlog = new EsBlog(returnBlog);
        } else {
            esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
            esBlog.update(returnBlog);
        }

        esBlogService.updateEsBlog(esBlog);
        return returnBlog;
    }

    @Transactional
    @Override
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
        EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
        esBlogService.removeEsBlog(esblog.getId());
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.getOne(id);
    }

    @Override
    public Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable) {
        //模糊查询
        title = "%" + title + "%";
        String tags = title;
        Page<Blog> blogs = blogRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title,user,tags,user,pageable);

        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable) {
        //模糊查询
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
        return blogs;
    }
    @Override
    public void readingIncrease(Long id) {
        Blog blog = blogRepository.getOne(id);
        blog.setReadSize(blog.getReadSize() + 1);
        this.saveBlog(blog);
    }

    @Override
    public Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable) {
        return blogRepository.findByCatalog(catalog, pageable);
    }

    @Override
    public Blog createComment(Long blogId, String commentContent) {
        Blog originalBlog = blogRepository.getOne(blogId);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = new Comment(user, commentContent);
        if (originalBlog != null) {
            originalBlog.addComment(comment);
        }
        return this.saveBlog(originalBlog);
    }

    @Override
    public void removeComment(Long blogId, Long commentId) {
        Blog originalBlog = blogRepository.findById(blogId).orElse(null);
        if (originalBlog != null) {
            originalBlog.removeComment(commentId);
        }
        this.saveBlog(originalBlog);
    }

    @Override
    public Blog createVote(Long blogId) {
        Blog originalBlog = blogRepository.findById(blogId).orElse(null);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Vote vote = new Vote(user);
        boolean isExist = false;
        if (originalBlog != null) {
            isExist = originalBlog.addVote(vote);
        }
        if (isExist) {
            throw new IllegalArgumentException("该用户已经点过赞了");
        }
        return this.saveBlog(originalBlog);
    }

    @Override
    public void removeVote(Long blogId, Long voteId) {
        Blog originalBlog = blogRepository.findById(blogId).orElse(null);
        if (originalBlog != null) {
            originalBlog.removeVote(voteId);
        }
        this.saveBlog(originalBlog);
    }
}
