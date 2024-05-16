package com.example.baseballprediction.domain.team.controller;

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
import com.example.baseballprediction.domain.team.service.TeamService;

@WebMvcTest(TeamController.class)
class TeamControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TeamService teamService;

	@DisplayName("팀 목록을 조회한다.")
	@WithTestUser
	@Test
	void teamList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/teams")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}
}