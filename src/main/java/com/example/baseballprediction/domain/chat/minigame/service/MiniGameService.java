package com.example.baseballprediction.domain.chat.minigame.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.baseballprediction.domain.chat.dto.ChatProfileDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.Options;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteCreator;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteMessage;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteRatio;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteResult;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteResultDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteResultDTO;
import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;
import com.example.baseballprediction.domain.chat.minigame.repository.MiniGameRepository;
import com.example.baseballprediction.domain.chat.minigamevote.entity.MiniGameVote;
import com.example.baseballprediction.domain.chat.minigamevote.repository.MiniGameVoteRepository;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.Status;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MiniGameService {
	
	
    public static final int MAX_VOTE_LIMIT = 40;
    public static final int REQUIRED_TOKENS_FOR_VOTE = 5;
	
	private final MiniGameRepository miniGameRepository;
	private final MiniGameVoteRepository miniGameVoteRepository;
	private final MemberRepository memberRepository;
	private final Map<Long, Map<String, Integer>> voteRecords = new ConcurrentHashMap<>();
	private Map<Long, Integer> voteCountPerGame = new ConcurrentHashMap<>();
	private final GameRepository gameRepository;
	private final SimpMessageSendingOperations messagingTemplate;
	private final Map<Long, Object> gameLocks = new ConcurrentHashMap<>();


	public MiniGame saveCreateVote(Long gameId,Options options,String nickname) {
		 
		int currentVoteCount = voteCountPerGame.getOrDefault(gameId, 0);
		if (currentVoteCount > MAX_VOTE_LIMIT) {
		    throw new BusinessException(ErrorCode.MINI_GAME_MAX_VOTE_LIMIT);
		}
		 Member member = memberRepository.findByNickname(nickname)
			        .orElseThrow(() ->  new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
		
		 if (member.getToken() < REQUIRED_TOKENS_FOR_VOTE) {
		        throw new BusinessException(ErrorCode.MINI_GAME_TOKENS_INSUFFICIENT);
		}
		 
		 member.addToken(-REQUIRED_TOKENS_FOR_VOTE);
		 memberRepository.save(member);
		 
		 Game game = gameRepository.findById(gameId)
		            .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));
		 
		// 여기서는 옵션을 단순히 저장하지 않고, gameId로 새로운 투표 세션만 생성함.
		voteRecords.putIfAbsent(gameId, new ConcurrentHashMap<>());
		MiniGame miniGame = MiniGame.builder()
		        .member(member)
		        .game(game)
		        .question(options.getQuestion())
		        .option1(options.getOption1())
		        .option2(options.getOption2())
		        .build();
		boolean canStart = findStartNewVote(gameId);
		
		//canStart가 참일 경우 READY -> PROGRESS 변경
		if (canStart) {
		    miniGame.updateStatus(Status.PROGRESS); 
		}
		
		
		MiniGame savedMiniGame = miniGameRepository.save(miniGame);
		
		voteCountPerGame.put(gameId, currentVoteCount + 1);
	
		return savedMiniGame;
	}
	 
	private boolean findStartNewVote(Long gameId) {
	    if (!miniGameRepository.findByGameIdAndStatus(gameId, Status.PROGRESS).isEmpty()) {
	        return false;
	    }

	    List<MiniGame> readyVotes = miniGameRepository.findByGameIdAndStatusOrderByCreatedAtAsc(gameId, Status.READY);
	    
	    if (!readyVotes.isEmpty()) {
	        return false;
	    }

	    return true;
	}
	
	// 1분마다 실행 3분마다 진행시 스케줄러 반복이 엇나갈걸 대비.
	@Scheduled(cron = "0 * 13-23 * * ?", zone = "Asia/Seoul") 
	public void modifyCheckAndUpdateVoteStatus() {
		
	    List<Long> gameIds = gameRepository.findGameIdAndStatus(); 

	    for (Long gameId : gameIds) {
	    	// gameIds가 돌면서 같은 gameId에 작업을 할려고 할 때, 접근을 막는다.
	    	synchronized(getGameLock(gameId)) {
		        List<MiniGame> inProgressVotes = miniGameRepository.findByGameIdAndStatus(gameId, Status.PROGRESS);
		        List<MiniGame> readyVotes = miniGameRepository.findByGameIdAndStatusOrderByCreatedAtAsc(gameId, Status.READY);
	
		        LocalDateTime now = LocalDateTime.now();
	
		        for (MiniGame vote : inProgressVotes) {
		            if (Duration.between(vote.getStartedAt(), now).toMinutes() >= 3) {
		            	SaveVoteEndResults(vote.getId());		                
		            }
		        }
	
		        if (inProgressVotes.isEmpty() && !readyVotes.isEmpty()) {
		            MiniGame nextVote = readyVotes.get(0);
		            modifyVoteStatus(nextVote, Status.PROGRESS);
		            ChatProfileDTO profile = nextVote.toChatProfileDTO(); 
		            Options options = nextVote.toOptions(); 
		            LocalDateTime startedAt = nextVote.getStartedAt();
	            	messagingTemplate.convertAndSend("/sub/chat/" + nextVote.getGame().getId(), new VoteMessage(nextVote.getId(), "투표가 시작되었습니다.", profile, options,startedAt));
		        }
	    	}
	    }
	}
	
	public void SaveVoteEndResults(Long miniGameId) {
		MiniGameVoteResultDTO voteResults = miniGameVoteRepository.findVoteRatiosAndMemberId(miniGameId);
	    MiniGame miniGame = miniGameRepository.findById(miniGameId)
	        .orElseThrow(() -> new BusinessException(ErrorCode.MINI_GAME_NOT_FOUND));

	    if (miniGame.getStatus() == Status.END) {
	    	 throw new BusinessException(ErrorCode.MINI_GAME_ALREADY_ENDED);
	    }

	    miniGame.updateStatus(Status.END);
	    miniGameRepository.save(miniGame);

	    String resultMessage = String.format("%s 투표 결과: %s %d%%, %s %d%%로 마감되었습니다.",
                miniGame.getQuestion(),
                miniGame.getOption1(), voteResults.getOption1VoteRatio(),
                miniGame.getOption2(), voteResults.getOption2VoteRatio());
	    

	    messagingTemplate.convertAndSend("/sub/chat/" + miniGame.getGame().getId(), new VoteResult(resultMessage));
	}

	private void modifyVoteStatus(MiniGame vote, Status status) {
	    vote.updateStatus(status);
	    if (status == Status.PROGRESS) {
	        vote.setStartedAt(LocalDateTime.now()); 
	    }
	    miniGameRepository.save(vote);
	}

	private synchronized Object getGameLock(Long gameId) {
	    return gameLocks.computeIfAbsent(gameId, k -> new Object());
	}
	 
    public boolean addVote(Long miniGameId, String nickname, int option) {
    	
    	MiniGame miniGame = miniGameRepository.findById(miniGameId)
    	        .orElseThrow(() -> new NotFoundException(ErrorCode.MINI_GAME_NOT_FOUND));
    	if (miniGame.getStatus() == Status.END) {
            throw new BusinessException(ErrorCode.MINI_GAME_ALREADY_ENDED);
        }
    	
    	if (miniGame.getStatus() == Status.READY) {
            throw new BusinessException(ErrorCode.MINI_GAME_CURRENTLY_WAITING);
        }
    	
        Map<String, Integer> gameVotes = voteRecords.getOrDefault(miniGameId, new ConcurrentHashMap<>());

        Optional<MiniGameVote> existingVote = miniGameVoteRepository.findByMiniGameIdAndMemberNickname(miniGameId, nickname);
        if (gameVotes.containsKey(nickname) && existingVote.isPresent()) {
        	 return false; // 이미 투표함
        }
        // 세션을 저장한다. 
        gameVotes.put(nickname, option);
        voteRecords.put(miniGameId, gameVotes);
        // db저장
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

        if (miniGame.getStatus() == Status.END) {
            throw new BusinessException(ErrorCode.MINI_GAME_ALREADY_ENDED);
        }
    	
    	if (miniGame.getStatus() == Status.READY) {
            throw new BusinessException(ErrorCode.MINI_GAME_CURRENTLY_WAITING);
        }
       // 사용자의 프로필 조회
       Member member = memberRepository.findByNickname(nickname)
           .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        
       MiniGameVote vote = miniGameVoteRepository.findByMiniGameIdAndMemberNickname(miniGameId, nickname)
		   .orElseThrow(() -> new BusinessException(ErrorCode.MINI_GAME_NOT_PARTICIPATED));
       
       
        // 투표율과, 투표 만들 사람을 조회
        MiniGameVoteResultDTO voteResults = miniGameVoteRepository.findVoteRatiosAndMemberId(miniGameId);

        // 미니게임 생성자 정보 조회 
        Member creator = memberRepository.findById(voteResults.getMemberId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        VoteCreator voteCreator = new VoteCreator(creator.getNickname(), new Options(miniGame.getQuestion(), miniGame.getOption1(), miniGame.getOption2()));
        ChatProfileDTO myProfile = new ChatProfileDTO(member.getNickname(), member.getProfileImageUrl(), member.getTeam().getName());
        
        VoteRatio ratio = new VoteRatio(voteResults.getOption1VoteRatio(), voteResults.getOption2VoteRatio());
        VoteResultDTO resultDTO = new VoteResultDTO(voteCreator, myProfile, ratio);
        
        return resultDTO;
    }
    
    //게임이 종료된 뒤 미니투표가 해당 gameId에 남아 있을경우 토큰 환불처리
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul")
    public void SaveCancelledVotesAndRefundTokens() {
    	List<Long> endedGameIds = gameRepository.findGameIdsByStatus(Status.END);

        for (Long gameId : endedGameIds) {
            synchronized (getGameLock(gameId)) {
            	List<Status> statuses = Arrays.asList(Status.READY, Status.PROGRESS);
                List<MiniGame> actionableMiniGames = miniGameRepository.findByGameIdAndStatusIn(gameId, statuses);
                if (actionableMiniGames.isEmpty()) {
                    continue; 
                }
                actionableMiniGames.forEach(miniGame -> {
                    miniGame.updateStatus(Status.CANCEL);
                    Member member = miniGame.getMember();
                    member.addToken(REQUIRED_TOKENS_FOR_VOTE);
                    memberRepository.save(member);
                });
                miniGameRepository.saveAll(actionableMiniGames);
            }
        }
    }
}