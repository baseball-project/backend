package com.example.baseballprediction.domain.chat.minigame.dto;

public interface MiniGameVoteResultDTO {
    Integer getOption1VoteRatio();
    Integer getOption2VoteRatio();
    Long getCreatorMemberId();
}