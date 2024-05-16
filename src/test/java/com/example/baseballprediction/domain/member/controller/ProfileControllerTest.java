package com.example.baseballprediction.domain.member.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.example.baseballprediction.annotation.WithTestUser;
import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.service.ProfileService;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProfileService profileService;

	@DisplayName("응원하는 팀을 수정한다.")
	@WithTestUser
	@Test
	void likeTeamModify() throws Exception {
		//given
		MemberRequest.LikeTeamDTO request = new MemberRequest.LikeTeamDTO(2);

		//when
		//then
		mockMvc.perform(put("/profile/team")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("프로필 이미지, 닉네임, 한마디를 수정한다.")
	@WithTestUser
	@Test
	void detailsModify() throws Exception {
		//given
		MemberRequest.DetailsDTO request = new MemberRequest.DetailsDTO("테스트유저11", "한마디!");
		MockMultipartFile profileImage = new MockMultipartFile("profileImage", "testImage.jpg", "jpg",
			"<<data>>".getBytes());

		//when
		//then
		mockMvc.perform(multipart(HttpMethod.PUT, "/profile/details")
				.file(new MockMultipartFile("data", "", "application/json",
					objectMapper.writeValueAsString(request).getBytes()))
				.file(profileImage)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("프로필 팝업을 조회한다.")
	@WithTestUser
	@Test
	void profileList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/profiles")
				.param("nickname", Mockito.anyString())
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@DisplayName("마이페이지 프로필 정보를 조회한다.")
	@WithTestUser
	@Test
	void detailsList() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get("/profile/details")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@DisplayName("닉네임 중복체크를 한다.")
	@WithTestUser
	@Test
	void nicknameExistList() throws Exception {
		//given
		String nickname = "test";
		//when
		//then
		mockMvc.perform(get("/profile/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.param("nickname", nickname))
			.andExpect(status().isOk());
	}

	@DisplayName("닉네임 중복체크를 한다. 20자 초과일 경우 예외가 발생한다.")
	@WithTestUser
	@Test
	void nicknameExistListWithLength() throws Exception {
		//given
		String nickname = "1234567891011121314151617";
		//when
		//then
		mockMvc.perform(get("/profile/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.param("nickname", nickname))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(ErrorCode.MEMBER_NICKNAME_LENGTH.getMessage()));

	}

	@DisplayName("닉네임 중복체크를 한다. 닉네임이 공백일 경우 예외가 발생한다.")
	@WithTestUser
	@Test
	void nicknameExistListWithEmpty() throws Exception {
		//given
		String nickname = "";
		//when
		//then
		mockMvc.perform(get("/profile/nickname-check")
				.contentType(MediaType.APPLICATION_JSON)
				.param("nickname", nickname))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(ErrorCode.MEMBER_NICKNAME_NULL.getMessage()));

	}
}