package com.example.baseballprediction.domain.team.controller;

import com.example.baseballprediction.domain.team.dto.TeamResponse;
import com.example.baseballprediction.domain.team.service.TeamService;
import com.example.baseballprediction.global.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.baseballprediction.domain.team.dto.TeamResponse.*;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/teams")
    public ResponseEntity<ApiResponse<List<TeamsDTO>>> teamList() {
        List<TeamsDTO> teams = teamService.findTeams();

        ApiResponse<List<TeamsDTO>> response = ApiResponse.success(teams);

        return ResponseEntity.ok(response);
    }

}
