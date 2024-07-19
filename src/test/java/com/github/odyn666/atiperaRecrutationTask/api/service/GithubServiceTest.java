package com.github.odyn666.atiperaRecrutationTask.api.service;


import com.github.odyn666.atiperaRecrutationTask.controller.GitHubTaskController;
import com.github.odyn666.atiperaRecrutationTask.dto.GitHubDTO;
import com.github.odyn666.atiperaRecrutationTask.exception.UserNotFoundException;
import com.github.odyn666.atiperaRecrutationTask.model.BranchModel;
import com.github.odyn666.atiperaRecrutationTask.model.CommitModel;
import com.github.odyn666.atiperaRecrutationTask.model.GitHubRepositoryModel;
import com.github.odyn666.atiperaRecrutationTask.model.GitHubUserModel;
import com.github.odyn666.atiperaRecrutationTask.service.GitHubService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(MockitoExtension.class)
public class GithubServiceTest {

    private static final Logger log = LoggerFactory.getLogger(GithubServiceTest.class);

    @Mock
    private RestTemplate restTemplate;

    @MockBean
    private GitHubService gitHubService;

    private static final String OWNER = "odyn666";
    private static final String REPO_NAME = "DrivingSchoolMenagmetSystem";
    private static final String GITHUB_USER_API = "https://api.github.com/users/";
    @Value("${github.token}")
    private String githubToken;

    /**
     * Initializes the GitHubService with the provided RestTemplate and API URLs.
     * It also initializes the GitHubTaskController with the initialized GitHubService.
     *
     * @param restTemplate      The RestTemplate used to make HTTP requests to the GitHub API.
     * @param githubUsersApiUrl The base URL for the GitHub API to retrieve user information.
     * @param githubReposApiUrl The base URL for the GitHub API to retrieve repository information.
     */
    @BeforeEach
    public void setup() {
        gitHubService = new GitHubService();  // Initialize with your constructor
        gitHubService.setRestTemplate(restTemplate);
        gitHubService.setGithubUsersApiUrl("https://api.github.com/users/");
        gitHubService.setGithubReposApiUrl("https://api.github.com/repos/");

        GitHubTaskController gitHubTaskController = new GitHubTaskController(gitHubService);
        // Initialize your GitHubService with your constructor
    }


    /**
     * This test method simulates a scenario where a user does not exist in the GitHub API.
     * It sends an HTTP GET request to the specified URL with the provided username and checks if the response status code is 404 (Not Found).
     * This indicates that the user does not exist in the GitHub API.
     *
     * @param username The username of the user to be searched in the GitHub API.
     * @throws UserNotFoundException Thrown when the user does not exist in the GitHub API.
     * @throws IOException           Thrown when an I/O error occurs while making the HTTP request.
     * @throws InterruptedException  Thrown when the current thread is interrupted while waiting for the HTTP request to complete.
     */
    @Test
    public void givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived()
            throws UserNotFoundException, IOException {

        // Given
        HttpUriRequest request = new HttpGet("http://localhost:8080/api/github/repositories?username=" + OWNER);
        request.addHeader("Accept", "application/json");

        //request.addHeader("Authorization", githubToken);


        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        log.info(httpResponse.toString());
        // Then
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_NOT_FOUND));
    }


    /**
     * This test method simulates a scenario where a user does not exist in the GitHub API.
     * It sends an HTTP GET request to the specified URL with the provided username and checks if the response status code is 404 (Not Found).
     * This indicates that the user does not exist in the GitHub API.
     *
     * @param username The username of the user to be searched in the GitHub API.
     * @throws UserNotFoundException Thrown when the user does not exist in the GitHub API.
     * @throws IOException           Thrown when an I/O error occurs while making the HTTP request.
     * @throws InterruptedException  Thrown when the current thread is interrupted while waiting for the HTTP request to complete.
     */
    @Test
    void givenIncorrectCredential_thenEmptyOptionalReceived() {
        GitHubUserModel userModel = new GitHubUserModel();
        userModel.setLogin("incorrectUsername321");
        Optional<GitHubUserModel> result = gitHubService.validateUsername(userModel.getLogin());

        Assertions.assertEquals(Optional.empty(), result);

    }

    /**
     * This test method simulates a scenario where a request header does not contain an Accept header.
     * It sends an HTTP GET request to the specified URL with the provided username and checks if the response's MIME type matches the expected header value.
     *
     * @param header The expected MIME type value for the response.
     * @throws IOException Thrown when an I/O error occurs while making the HTTP request.
     */
    @Test
    public void givenRequestHeaderWithNoAcceptHeader() throws IOException {

        String header = "application/json";
        HttpUriRequest request = new HttpGet("http://localhost:8080/api/github/repositories?username=odyn666");

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        log.info(response.toString());
        String mimetype = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(header, mimetype);

    }

    /**
     * This test method simulates a scenario where the GitHub API returns null branches data for a given repository.
     * It sends an HTTP GET request to the specified URL to retrieve the branches of a repository and checks if the returned list of branches is empty.
     *
     * @param OWNER     The owner of the repository.
     * @param REPO_NAME The name of the repository.
     */
    @Test
    public void testGetBranchesReturnsEmptyListWhenApiReturnsNull() {
        // Given
        when(restTemplate.getForObject("https://api.github.com/repos/" + OWNER + "/" + REPO_NAME + "/branches", BranchModel[].class))
                .thenReturn(null);

        // When
        List<BranchModel> branches = gitHubService.getBranches(OWNER, REPO_NAME);

        // Then
        assertNotNull(branches);
        assertEquals(0, branches.size());
    }

    /**
     * This test method simulates a scenario where the GitHub API returns branches data for a given repository.
     * It sends an HTTP GET request to the specified URL to retrieve the branches of a repository and checks if the returned list of branches is not empty.
     *
     * @param OWNER     The owner of the repository.
     * @param REPO_NAME The name of the repository.
     */
    @Test
    public void testGetBranchesReturnsBranchesWhenApiReturnsData() {
        // Given
        BranchModel branchModel = new BranchModel();
        branchModel.setName("TrainerController");
        ;
        CommitModel commitModel = new CommitModel();
        commitModel.setSha("0a1845f4915ea94734cfe10b6e184c9920672b11");
        branchModel.setCommit(commitModel);


        BranchModel[] branches = new BranchModel[1];
        branches[0] = branchModel;
        when(restTemplate.getForObject("https://api.github.com/repos/" + OWNER + "/" + REPO_NAME + "/branches", BranchModel[].class))
                .thenReturn(branches);

        // When
        List<BranchModel> result = gitHubService.getBranches(OWNER, REPO_NAME);

        // Then
        assertEquals(1, result.size());
        assertEquals(branches[0].getName(), result.get(0).getName());
    }

    /**
     * This method retrieves a list of GitHub DTOs for the given owner.
     * It sends an HTTP GET request to the GitHub API to retrieve the owner's repositories and then converts each repository into a GitHub DTO.
     * The list of DTOs is then returned.
     *
     * @param owner The owner of the repositories to retrieve.
     * @return A list of GitHub DTOs representing the owner's repositories.
     */
    @Test
    void testGetDTOs_validResponse_returnsListOfDTOs() {
        // Arrange
        GitHubRepositoryModel[] repositories = new GitHubRepositoryModel[1];
        repositories[0] = new GitHubRepositoryModel();
        repositories[0].setName("repo1");
        repositories[0].setOwner(new GitHubUserModel());
        repositories[0].getOwner().setLogin(OWNER);
        repositories[0].setBranches(List.of(new BranchModel()));

        when(restTemplate.getForObject(GITHUB_USER_API + OWNER + "/repos?type=all", GitHubRepositoryModel[].class))
                .thenReturn(repositories);

        // Act
        List<GitHubDTO> dtoList = gitHubService.getDTOs(OWNER);

        // Assert
        assertNotNull(dtoList);
        assertEquals(1, dtoList.size());
        GitHubDTO dto = dtoList.get(0);
        assertEquals(OWNER, dto.ownerLogin());
        assertEquals("repo1", dto.RepositoryName());
        log.info(dto.RepositoryName());
        assertNotNull(dto.branch());
    }

    /**
     * This method retrieves a list of GitHub DTOs for the given owner.
     * It sends an HTTP GET request to the GitHub API to retrieve the owner's repositories and then converts each repository into a GitHub DTO.
     * The list of DTOs is then returned.
     *
     * @param owner The owner of the repositories to retrieve.
     * @return A list of GitHub DTOs representing the owner's repositories.
     */
    @Test
    void testGetDTOs_invalidResponse_returnsEmptyList() {
        // Arrange
        when(restTemplate.getForObject(GITHUB_USER_API + OWNER + "/repos?type=all", GitHubRepositoryModel[].class))
                .thenReturn(null);

        // Act
        List<GitHubDTO> dtoList = gitHubService.getDTOs(OWNER);

        // Assert
        assertNotNull(dtoList);
        assertEquals(0, dtoList.size());
    }

    /**
     * This method retrieves a list of GitHub DTOs for the given owner.
     * It sends an HTTP GET request to the GitHub API to retrieve the owner's repositories and then converts each repository into a GitHub DTO.
     * The list of DTOs is then returned.
     *
     * @param owner The owner of the repositories to retrieve.
     * @return A list of GitHub DTOs representing the owner's repositories.
     */
    @Test
    void testGetDTOs_emptyResponse_returnsEmptyList() {
        // Arrange
        when(restTemplate.getForObject(GITHUB_USER_API + OWNER + "/repos?type=all", GitHubRepositoryModel[].class))
                .thenReturn(new GitHubRepositoryModel[0]);

        // Act
        List<GitHubDTO> dtoList = gitHubService.getDTOs(OWNER);

        // Assert
        assertNotNull(dtoList);
        assertEquals(0, dtoList.size());
    }

    /**
     * This method retrieves a list of GitHub DTOs for the given owner.
     * It sends an HTTP GET request to the GitHub API to retrieve the owner's repositories and then converts each repository into a GitHub DTO.
     * The list of DTOs is then returned.
     *
     * @param owner The owner of the repositories to retrieve.
     * @return A list of GitHub DTOs representing the owner's repositories.
     */
    @Test
    void testGetDTOs_nullUsername_returnsEmptyList() {
        // Arrange
        String nullUsername = null;

        // Act
        List<GitHubDTO> dtoList = gitHubService.getDTOs(nullUsername);

        // Assert
        assertNotNull(dtoList);
        assertEquals(0, dtoList.size());
    }

    /**
     * This method retrieves a list of GitHub DTOs for the given owner.
     * It sends an HTTP GET request to the GitHub API to retrieve the owner's repositories and then converts each repository into a GitHub DTO.
     * The list of DTOs is then returned.
     *
     * @param owner The owner of the repositories to retrieve.
     * @return A list of GitHub DTOs representing the owner's repositories.
     */
    @Test
    void testGetDTOs_emptyUsername_returnsEmptyList() {
        // Arrange
        String emptyUsername = "";

        // Act
        List<GitHubDTO> dtoList = gitHubService.getDTOs(emptyUsername);

        // Assert
        assertNotNull(dtoList);
        assertEquals(0, dtoList.size());
    }
}
