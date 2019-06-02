package com.bbblog.service;

import com.bbblog.entity.Vote;
import com.bbblog.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImp implements VoteService {
    @Autowired
    private VoteRepository voteRepository;
    @Override
    public Vote getVoteById(Long id) {
        return voteRepository.getOne(id);
    }

    @Override
    public void removeVote(Long id) {

        voteRepository.deleteById(id);
    }
}
