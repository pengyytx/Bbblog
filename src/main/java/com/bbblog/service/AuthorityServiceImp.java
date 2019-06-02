package com.bbblog.service;

import com.bbblog.entity.Authority;
import com.bbblog.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImp implements AuthorityService {
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Authority getAuthorityById(Long id) {
        return authorityRepository.getOne(id);
    }
}
