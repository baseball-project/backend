package com.example.baseballprediction.domain.reply.controller;

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
import com.example.baseballprediction.domain.reply.dto.ReplyReportRequest;
import com.example.baseballprediction.domain.reply.service.ReplyService;
import com.example.baseballprediction.global.constant.ReportType;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ReplyController.class)
class ReplyControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ReplyService replyService;

	@DisplayName("")
	@WithTestUser
	@Test
	void replyRemove() throws Exception {
		//given
		Long replyId = 1L;

		//when
		//then
		mockMvc.perform(delete("/replies/" + replyId)
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("댓글 신고 항목을 조회한다.")
	@WithTestUser
	@Test
	void replyReportList() throws Exception {
		//given
		Long replyId = 1L;

		//when
		//then
		mockMvc.perform(get("/replies/" + replyId + "/report")
				.contentType(APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("댓글 신고를 등록한다.")
	@WithTestUser
	@Test
	void replyReportAdd() throws Exception {
		//given
		Long replyId = 1L;
		ReplyReportRequest.ReportDTO request = new ReplyReportRequest.ReportDTO(ReportType.ETC);

		//when
		//then
		mockMvc.perform(post("/replies/" + replyId + "/report")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isCreated());
	}
}