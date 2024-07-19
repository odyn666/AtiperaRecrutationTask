package com.github.odyn666.atiperaRecrutationTask.controller;

import com.github.odyn666.atiperaRecrutationTask.dto.GitHubDTO;
import com.github.odyn666.atiperaRecrutationTask.exception.BadHeaderException;
import com.github.odyn666.atiperaRecrutationTask.exception.UserNotFoundException;
import com.github.odyn666.atiperaRecrutationTask.model.GitHubUserModel;
import com.github.odyn666.atiperaRecrutationTask.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {GitHubTaskController.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DisabledInAotMode
@WebMvcTest(GitHubTaskController.class)
class GitHubTaskControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService gitHubService;

    @Autowired
    @InjectMocks
    private GitHubTaskController gitHubTaskController;


    /**
     * Method under test:
     * {@link GitHubTaskController#getGitHubRepositories(String, String)}
     */
    @Test
    void testGetGitHubRepositories() throws Exception {
        // Arrange
        GitHubUserModel gitHubUserModel = new GitHubUserModel();
        gitHubUserModel.setLogin("Login");
        Optional<GitHubUserModel> ofResult = Optional.of(gitHubUserModel);
        when(gitHubService.getDTOs(Mockito.<String>any())).thenReturn(new ArrayList<>());
        when(gitHubService.validateUsername(Mockito.<String>any())).thenReturn(ofResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/github/repositories")
                .param("username", "foo")
                .header("Accept", "application/json");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(gitHubTaskController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("[]"));
    }

    /**
     * Method under test:
     * {@link GitHubTaskController#getGitHubRepositories(String, String)}
     */
    @Test
    void testGetGitHubRepositories2() throws Exception {
        // Arrange
        GitHubUserModel gitHubUserModel = new GitHubUserModel();
        gitHubUserModel.setLogin("Login");
        Optional<GitHubUserModel> ofResult = Optional.of(gitHubUserModel);

        ArrayList<GitHubDTO> gitHubDTOList = new ArrayList<>();
        gitHubDTOList.add(new GitHubDTO("application/json", "application/json", new ArrayList<>()));
        when(gitHubService.getDTOs(Mockito.<String>any())).thenReturn(gitHubDTOList);
        when(gitHubService.validateUsername(Mockito.<String>any())).thenReturn(ofResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/github/repositories")
                .param("username", "foo")
                .header("Accept", "application/json");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(gitHubTaskController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content()
                        .string("[{\"RepositoryName\":\"application/json\",\"ownerLogin\":\"application/json\",\"branch\":[]}]"));
    }

    /**
     * This test method verifies the behavior of the {@code getGitHubRepositories} method in the {@code GitHubTaskController} class when a user is not found.
     * It asserts that a {@code UserNotFoundException} is thrown with the message "USER NOT FOUND" when an invalid username is provided.
     *
     * @param username     The username to be used for the test.
     * @param acceptHeader The accept header to be used for the test.
     * @throws Exception If any unexpected exception occurs during the test.
     */
    @Test
    void userNotFoundExceptionTestInGitHubControllerClass() throws Exception {
        String username = "randomUsernameForTestPurposes789321";

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> gitHubTaskController.getGitHubRepositories(username, "application/json"));

        assertEquals("USER NOT FOUND", exception.getMessage());
    }

    /**
     * This test method verifies the behavior of the {@code getGitHubRepositories} method in the {@code GitHubTaskController} class when an invalid accept header is provided.
     * It asserts that a {@code BadHeaderException} is thrown when the accept header is null or empty.
     *
     * @param username      The username of the GitHub user whose repositories are to be retrieved.
     * @param acceptHeader= The accept header specifying the desired format of the response.
     * @throws BadHeaderException If the accept header is null or empty.
     */
    @Test
    void testGetGitHubRepositoriesWithNullAcceptHeader() {
        // Arrange
        String username = "odyn666";
        String acceptHeader = "";

        // Act & Assert
        assertThrows(BadHeaderException.class, () -> gitHubTaskController.getGitHubRepositories(username, acceptHeader));
    }

}

