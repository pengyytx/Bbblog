package com.bbblog.controller;
import com.bbblog.entity.Authority;
import com.bbblog.entity.User;
import com.bbblog.service.AuthorityService;
import com.bbblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页控制器
 */

@Controller
public class MainController {
    private static final Long ROLE_USER_AUTHORITY_ID = 2L;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    @GetMapping("/")
    public String root(){
        return "redirect:index";
    }
    @GetMapping("/index")
    public String index(){
        return "redirect:blogs";
    }


    @GetMapping("/register")
    public String register(){
        return "register";
    }
    /**
     * 注册用户
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String registerUser(User user) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
        user.setAuthorities(authorities);
        //Spring Security 提供了BCryptPasswordEncoder类,实现Spring的PasswordEncoder接口使用BCrypt强哈希方法来加密密码
        //BCrypt强哈希方法 每次加密的结果都不一样
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(user.getPassword().trim());
        user.setPassword(encode);
        userService.saveUser(user);
        return "redirect:/login";
    }
    /**
     * 获取登录界面
     * @return
     */
    @GetMapping("/login")
    public String login(){
        return "/login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError",true);
        model.addAttribute("errorMsg","登录失败，用户名或密码错误！");
        return "login";
    }


}
