package com.bbblog.repository;

import com.bbblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * 根据用户名称分页查询用户列表
     * @param name
     * @param pageable
     * @return
     */
    Page<User> findByNameLike(String name,Pageable pageable);

    /**
     * 根据用户账号查询用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    List<User> findByUsernameIn(Collection<String> usernames);
}
