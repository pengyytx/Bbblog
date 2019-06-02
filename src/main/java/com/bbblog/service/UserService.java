package com.bbblog.service;

import com.bbblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {

    User saveUser(User user);

    void removeUser(Long id);

    void removeUsersInBatch(List<User> users);

    User updateUser(User user);

    User getUserById(Long id);

    List<User> listUsers();

    Page<User> listUsersByNameLike(String name, Pageable pageable);

    List<User> listUsersByUserNames(Collection<String> userNames);

}
