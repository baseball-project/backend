package com.example.baseballprediction.domain.member.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.baseballprediction.annotation.WithTestUser;
import com.example.baseballprediction.domain.member.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private HistoryService historyService;

	@DisplayName("요정 통계를 조회한다.")
	@WithTestUser
	@Test
	void fairyStatisticList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/profile/history/fairy-statistics")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("승리요정 내역을 조회한다.")
	@WithTestUser
	@Test
	void fairyHistoryList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/profile/history/votes")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("선물 내역을 조회한다.")
	@WithTestUser
	@Test
	void giftHistoryList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/profile/history/gifts")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}
}