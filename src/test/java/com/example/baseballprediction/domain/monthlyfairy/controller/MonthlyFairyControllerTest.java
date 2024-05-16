package com.example.baseballprediction.domain.monthlyfairy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.baseballprediction.domain.monthlyfairy.service.MonthlyFairyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MonthlyFairyController.class)
class MonthlyFairyControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MonthlyFairyService monthlyFairyService;

	@DisplayName("월간 승리요정 목록을 조회한다.")
	@WithMockUser(value = "test")
	@Test
	void monthlyFairyList() throws Exception {
		///given
		//when
		//then
		mockMvc.perform(get("/statistics")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
}