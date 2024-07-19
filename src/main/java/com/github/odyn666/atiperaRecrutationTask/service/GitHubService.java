package com.github.odyn666.atiperaRecrutationTask.service;

import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import com.github.odyn666.atiperaRecrutationTask.dto.GitHubDTO;
import com.github.odyn666.atiperaRecrutationTask.exception.UserNotFoundException;
import com.github.odyn666.atiperaRecrutationTask.model.BranchModel;
import com.github.odyn666.atiperaRecrutationTask.model.GitHubRepositoryModel;
import com.github.odyn666.atiperaRecrutationTask.model.GitHubUserModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class GitHubService {
    @Value("${github.api.url.users}")
    private String githubUsersApiUrl;
    @Value("${github.api.url.repos}")
    private String githubReposApiUrl;
    private RestTemplate restTemplate = new RestTemplate();


    public Optional<GitHubUserModel> validateUsername(String username) {
        String url = githubUsersApiUrl + username;

        return Optional.ofNullable(restTemplate.getForObject(url, GitHubUserModel.class));

    }

    /**
     * Retrieves the GitHub DTOs for the specified user's repositories.
     *
     * @param username The username of the GitHub user whose repositories will be fetched.
     * @return A list of {@link GitHubDTO} objects representing the user's repositories, along with their branches.
     */
    public List<GitHubDTO> getDTOs(String username) {

        String url = githubUsersApiUrl + username + "/repos?type=all";
        GitHubRepositoryModel[] repositories = restTemplate.getForObject(url, GitHubRepositoryModel[].class);
        if (repositories != null) {
            return Arrays.stream(repositories)
                    .filter(repository -> repository.getOwner().getLogin().equals(username))
                    .filter(r -> !r.isFork())
                    .map(r -> {
                        return GitHubDTO.builder()
                                .RepositoryName(r.getName())
                                .ownerLogin(r.getOwner().getLogin())
                                .branch(addBranchesToGithubRepository(r).getBranches())
                                .build();
                    }).toList();
        }
        return List.of();
    }

    /**
     * Adds the list of branches for the specified GitHub repository to the repository object.
     *
     * @param repository The GitHub repository object to which the branches will be added.
     * @return The updated GitHub repository object with the list of branches.
     */
    private GitHubRepositoryModel addBranchesToGithubRepository(GitHubRepositoryModel repository) {
        List<BranchModel> branches = getBranches(repository.getOwner().getLogin(), repository.getName());
        repository.setBranches(branches);
        return repository;
    }

    /**
     * Retrieves the list of branches for a specific GitHub repository.
     *
     * @param owner    The owner of the repository.
     * @param repoName The name of the repository.
     * @return A list of {@link BranchModel} objects representing the branches of the specified repository.
     * If no branches are found, an empty list is returned.
     */
    public List<BranchModel> getBranches(String owner, String repoName) {
        String url = githubReposApiUrl + owner + "/" + repoName + "/branches";
        BranchModel[] branches = restTemplate.getForObject(url, BranchModel[].class);
        if (branches != null) {
            return Arrays.asList(branches);
        }
        return List.of();
    }


}
