package com.bbblog.service;

import com.bbblog.entity.Vote;

public interface VoteService {
    Vote getVoteById(Long id);

    void removeVote(Long id);
}
