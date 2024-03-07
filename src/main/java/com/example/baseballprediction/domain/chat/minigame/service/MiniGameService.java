package com.example.baseballprediction.domain.chat.minigame.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.baseballprediction.domain.chat.dto.ChatProfileDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.Options;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteCreator;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteRatio;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteResultDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteResultDTO;
import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;
import com.example.baseballprediction.domain.chat.minigame.repository.MiniGameRepository;
import com.example.baseballprediction.domain.chat.minigamevote.entity.MiniGameVote;
import com.example.baseballprediction.domain.chat.minigamevote.repository.MiniGameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MiniGameService {
	
	 private final MiniGameRepository miniGameRepository;
	 private final MiniGameVoteRepository miniGameVoteRepository;
	 private final MemberRepository memberRepository;
	 private final Map<Long, Map<String, Integer>> voteRecords = new ConcurrentHashMap<>();

	 public MiniGame saveCreateVote(Long gameId,Options options,String nickname) {
		 
		 Member member = memberRepository.findByNickname(nickname)
			        .orElseThrow(() ->  new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		 if (member.getToken() < 5) {
		        throw new BusinessException(ErrorCode.MINI_GAME_TOKENS_INSUFFICIENT);
	    }
		 
		 member.addToken(-5);
		 memberRepository.save(member);
		 
        // 여기서는 옵션을 단순히 저장하지 않고, gameId로 새로운 투표 세션만 생성함.
        voteRecords.putIfAbsent(gameId, new ConcurrentHashMap<>());
        MiniGame miniGame = MiniGame.builder()
        				.creator(member)
        				.question(options.getQuestion())
        				.option1(options.getOption1())
        				.option2(options.getOption2())
        				.build();
        
        MiniGame savedMiniGame = miniGameRepository.save(miniGame);
        
        return savedMiniGame;
    }
	 
    public boolean addVote(Long miniGameId, String nickname, int option) {
        Map<String, Integer> gameVotes = voteRecords.getOrDefault(miniGameId, new ConcurrentHashMap<>());

        Optional<MiniGameVote> existingVote = miniGameVoteRepository.findByMiniGameIdAndMemberNickname(miniGameId, nickname);
        if (gameVotes.containsKey(nickname) && existingVote.isPresent()) {
        	 return false; // 이미 투표함
        }
        // 세션을 저장한다. 
        gameVotes.put(nickname, option);
        voteRecords.put(miniGameId, gameVotes);
        // db저장
        MiniGame miniGame = miniGameRepository.findById((long) miniGameId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.MINI_GAME_NOT_FOUND));
        Member member = memberRepository.findByNickname(nickname)
            .orElseThrow(() ->  new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        MiniGameVote vote = MiniGameVote.builder()
            .miniGame(miniGame)
            .member(member)
            .voteOption(option)
            .build();
        miniGameVoteRepository.save(vote);

        return true; // 투표 성공
    }
	
    @Transactional(readOnly = true) 
	public VoteResultDTO findPerformVoteAndGetResults(Long miniGameId, String nickname) {

        // 미니게임이 존재하는지 확인
        MiniGame miniGame = miniGameRepository.findById(miniGameId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MINI_GAME_NOT_FOUND));

       // 사용자의 프로필 조회
       Member member = memberRepository.findByNickname(nickname)
           .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        
       MiniGameVote vote = miniGameVoteRepository.findByMiniGameIdAndMemberNickname(miniGameId, nickname)
		   .orElseThrow(() -> new BusinessException(ErrorCode.MINI_GAME_NOT_PARTICIPATED));
       
       
        // 투표율과, 투표 만들 사람을 조회
        MiniGameVoteResultDTO voteResults = miniGameVoteRepository.findVoteRatiosAndCreatorMemberId(miniGameId);

        // 미니게임 생성자 정보 조회 
        Member creator = memberRepository.findById(voteResults.getCreatorMemberId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        VoteCreator voteCreator = new VoteCreator(creator.getNickname(), new Options(miniGame.getQuestion(), miniGame.getOption1(), miniGame.getOption2()));
        ChatProfileDTO myProfile = new ChatProfileDTO(member.getNickname(), member.getProfileImageUrl(), member.getTeam().getName());
        
        VoteRatio ratio = new VoteRatio(voteResults.getOption1VoteRatio(), voteResults.getOption2VoteRatio());
        VoteResultDTO resultDTO = new VoteResultDTO(voteCreator, myProfile, ratio);
        
        return resultDTO;
    }

    


}
