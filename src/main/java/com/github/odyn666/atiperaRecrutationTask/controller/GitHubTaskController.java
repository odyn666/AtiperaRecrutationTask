package com.github.odyn666.atiperaRecrutationTask.controller;

import com.github.odyn666.atiperaRecrutationTask.dto.GitHubDTO;
import com.github.odyn666.atiperaRecrutationTask.exception.BadHeaderException;
import com.github.odyn666.atiperaRecrutationTask.exception.UserNotFoundException;
import com.github.odyn666.atiperaRecrutationTask.model.GitHubUserModel;
import com.github.odyn666.atiperaRecrutationTask.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GitHubTaskController {
    private final GitHubService gitHubService;

    @GetMapping("/github/repositories")
    public ResponseEntity<List<GitHubDTO>> getGitHubRepositories(
            @RequestParam String username,
            @RequestHeader("Accept") String acceptHeader
    ) {

        if (!acceptHeader.equals("application/json")) {
            throw new BadHeaderException(HttpStatus.NOT_FOUND.value(), "INVALID ACCEPT HEADER");
        }

        GitHubUserModel gitHubUserModel = gitHubService.validateUsername(username).orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND.value(), "USER NOT FOUND"));
        List<GitHubDTO> repositories = gitHubService.getDTOs(gitHubUserModel.getLogin());

        return ResponseEntity.ok(repositories);
    }


}
