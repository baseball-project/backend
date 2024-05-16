package com.example.baseballprediction.domain.game.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.baseballprediction.annotation.WithTestUser;
import com.example.baseballprediction.domain.game.service.GameService;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRequest;
import com.example.baseballprediction.domain.gamevote.service.GameVoteService;
import com.example.baseballprediction.domain.reply.dto.ReplyRequest;
import com.example.baseballprediction.domain.reply.service.ReplyService;
import com.example.baseballprediction.domain.replylike.service.ReplyLikeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(GameController.class)
class GameControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private GameService gameService;

	@MockBean
	private ReplyService replyService;

	@MockBean
	private ReplyLikeService replyLikeService;

	@MockBean
	private GameVoteService gameVoteService;

	@DisplayName("오늘의 승부예측 경기 목록을 조회한다.")
	@WithTestUser
	@Test
	void gameDailyTodayList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/games")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("오늘의 승부예측 댓글 목록을 조회한다.")
	@WithTestUser
	@Test
	void replyGameList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/games/daily-replies")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("오늘의 승부예측 댓글을 작성한다.")
	@WithTestUser
	@Test
	void gameReplyAdd() throws Exception {
		//given
		ReplyRequest.ReplyDTO request = new ReplyRequest.ReplyDTO("댓글");

		//when
		//then
		mockMvc.perform(post("/games/daily-reply")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isCreated());
	}

	@DisplayName("오늘의 승부예측 댓글 좋아요를 등록한다.")
	@WithTestUser
	@Test
	void gameReplyLikeAdd() throws Exception {
		//given
		Long replyId = 1L;

		//when
		//then
		mockMvc.perform(post("/games/daily-reply/" + replyId + "/like")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("오늘의 승부예측 댓글 좋아요를 취소한다.")
	@WithTestUser
	@Test
	void gameReplyLikeCancel() throws Exception {
		//given
		Long replyId = 1L;

		//when
		//then
		mockMvc.perform(delete("/games/daily-reply/" + replyId + "/like")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("오늘의 승부예측 투표를 등록한다.")
	@WithTestUser
	@Test
	void gameVoteAdd() throws Exception {
		//given
		GameVoteRequest.GameVoteRequestDTO request = new GameVoteRequest.GameVoteRequestDTO(1);

		//when
		//then
		mockMvc.perform(post("/games/1/vote")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isCreated());
	}

	@DisplayName("오늘의 승부예측 투표를 변경한다.")
	@WithTestUser
	@Test
	void gameVoteModify() throws Exception {
		//given
		GameVoteRequest.GameVoteRequestDTO request = new GameVoteRequest.GameVoteRequestDTO(1);

		//when
		//then
		mockMvc.perform(put("/games/voteUpdate/1")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("오늘의 승부예측 투표를 취소한다.")
	@WithTestUser
	@Test
	void gameVoteRemove() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(delete("/games/voteDelete/1")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("오늘의 승부예측 대댓글을 조회한다.")
	@WithTestUser
	@Test
	void replySubList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/games/daily-replies/1/sub")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("오늘의 승부예측 대댓글을 작성한다.")
	@WithTestUser
	@Test
	void replySubAdd() throws Exception {
		//given
		ReplyRequest.ReplyDTO request = new ReplyRequest.ReplyDTO("대댓글");

		//when
		//then
		mockMvc.perform(post("/games/daily-replies/1/sub")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isCreated());
	}

	@DisplayName("지난 승부예측을 조회한다.")
	@WithTestUser
	@Test
	void gameWeekList() throws Exception {
		//given
		String startDate = LocalDate.now().minusDays(6).toString();
		String endDate = LocalDate.now().toString();

		MultiValueMap<String, String> request = new LinkedMultiValueMap();
		request.put("startDate", List.of(startDate));
		request.put("endDate", List.of(endDate));

		//when
		//then
		mockMvc.perform(get("/games/past")
				.contentType(APPLICATION_JSON)
				.param("endDate", endDate)
				.params(request)
				.with(csrf()))
			.andExpect(status().isOk());
	}
}