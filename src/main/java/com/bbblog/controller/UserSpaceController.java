package com.bbblog.controller;

import com.bbblog.entity.Blog;
import com.bbblog.entity.Catalog;
import com.bbblog.entity.User;
import com.bbblog.entity.Vote;
import com.bbblog.service.BlogService;
import com.bbblog.service.CatalogService;
import com.bbblog.service.UserService;
import com.bbblog.vo.Response;
import com.bbblog.util.ConstrainViolationExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping("/u")
public class UserSpaceController {
    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CatalogService catalogService;

    //用户主页
    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username, Model model) throws UnsupportedEncodingException {
        User user = (User) userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return "redirect:/u/" + URLEncoder.encode(username, "UTF-8") + "/blogs";
    }
    /**
     * 获取个人设置页面
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, Model model) {
        User user = (User) userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        //model.addAttribute("fileSeverUrl",fileServerUrl);
        return new ModelAndView("userspace/profile", "userModel", model);
    }

    /**
     * 保存个人设置
     * @param username
     * @param user
     * @return
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public String saveProfile(@PathVariable("username") String username, User user) throws UnsupportedEncodingException {
        User originalUser = userService.getUserById(user.getId());
        originalUser.setEmail(user.getEmail());
        originalUser.setName(user.getName());
        //判断密码是否做了变更
        String rawPassword = originalUser.getPassword();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(user.getPassword());
        boolean isMatch = encoder.matches(rawPassword, encodePassword);
        if (!isMatch) {
            originalUser.setEncodePassword(user.getPassword());
        }
        userService.saveUser(originalUser);
        return "redirect:/u/" + URLEncoder.encode(username, "UTF-8") + "/profile";
    }

    /**
     * 获取编辑头像界面
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView avatar(@PathVariable("username") String username, Model model) {
        User user = (User) userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("userspace/avatar", "userModel", model);
    }

    /**
     * 保存头像
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
        String avatarUrl = user.getAvatar();

        User originalUser = userService.getUserById(user.getId());
        originalUser.setAvatar(avatarUrl);
        userService.saveUser(originalUser);

        return ResponseEntity.ok().body(new Response(true, "处理成功", avatarUrl));
    }


    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(@PathVariable("username") String username,
                                   @RequestParam(value = "order", required = false, defaultValue = "new") String order,
                                   @RequestParam(value = "catalog", required = false) Long catalogId,
                                   @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                   @RequestParam(value = "async", required = false) boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                   Model model) {

        User user = (User) userService.loadUserByUsername(username);

        Page<Blog> page = null;

        if (catalogId != null && catalogId > 0) { //分类查询
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByCatalog(catalog, pageable);
            order = "";
        } else if (order.equals("hot")) {  //最热查询
            Sort sort = new Sort(Sort.Direction.DESC, "readSize", "commentSize", "voteSize");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
        } else if (order.equals("new")) { //最新查询
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByTitleVote(user, keyword, pageable);
        }
      //  assert page != null;
        List<Blog> list = page.getContent();   //当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("catalogId", catalogId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);
        return (async ? "/userspace/u :: #mainContainerReplace" : "/userspace/u");
    }

    //获取博客展示界面
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
     //   System.out.println(id);
        User principal = null;
        Blog blog = blogService.getBlogById(id);

      //  System.out.println(blog);

        //每次读取，简单的可以认为阅读量+1
        blogService.readingIncrease(id);

        boolean isBlogOwner = false;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && username.equals(principal.getUsername())) {
                isBlogOwner = true;
            }
        }

        //判断操作用户点赞情况
        List<Vote> votes = blog.getVotes();
        Vote currentVote = null;

        if (principal != null) {
            for (Vote vote : votes) {
                vote.getUser().getUsername().equals(principal.getUsername());
                currentVote = vote;
                break;
            }
        }
        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("blogModel", blog);
        model.addAttribute("currentVote", currentVote);

        return "userspace/blog";
    }

//获取新增博客的界面
    @GetMapping("/{username}/blogs/edit")
    public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
        User user = (User) userService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }

    //获取编辑博客界面
    @GetMapping("/{username}/blogs/edit/{id}")
    public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        User user = (User) userService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", blogService.getBlogById(id));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }
    @GetMapping("/{username}/blogs/{id}")
    public String listBlogsByOrder( @PathVariable("id") Long id) {
        System.out.println("blogId:"+id);
        return "/userspace/blog";
    }

    //保存博客
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
        if (blog.getCatalog().getId() == null) {//对catalog进行空处理
            return ResponseEntity.ok().body(new Response(false, "未选择分类"));
        }
        try {
            if (blog.getId() != null) { //判断是修改还是新增
                Blog originalBlog = blogService.getBlogById(blog.getId());
                originalBlog.setTitle(blog.getTitle());
                originalBlog.setContent(blog.getContent());
                originalBlog.setHtmlContent(blog.getHtmlContent());
                originalBlog.setSummary(blog.getSummary());
                originalBlog.setCatalog(blog.getCatalog());
                originalBlog.setTags(blog.getTags());
                blogService.saveBlog(originalBlog);
            } else {
                User user = (User) userService.loadUserByUsername(username);
                blog.setUser(user);
                blogService.saveBlog(blog);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstrainViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    //删除博客
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {

        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

}
