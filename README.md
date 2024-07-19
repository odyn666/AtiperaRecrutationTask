# GitHub Recruitment Task

This project is a Spring Boot application that interacts with the GitHub API to retrieve information about GitHub users, their repositories, and branches. It includes a service (`GitHubService`) for handling GitHub-related operations and a controller (`GitHubTaskController`) for exposing API endpoints.

### Rsponse format
```json
    {
        "ownerLogin": "odyn666",
        "branch": [
            {
                "name": "master",
                "commit": {
                    "sha": "25f119899b73620607ab12f36f5ed1e219f008a1"
                }
            }
        ],
        "repositoryName": "Memory_game"
    }
```
```json
 {
  "ownerLogin": "odyn666",
  "branch": [
    {
      "name": "dev",
      "commit": {
        "sha": "e98f0eb31f163d86c66aa671f5cd135dddd1c4c3"
      }
    },
    {
      "name": "entities",
      "commit": {
        "sha": "09f1c5a66a4722e26a647372666a5f157c0d3f3f"
      }
    },
    {
      "name": "master",
      "commit": {
        "sha": "f865792e83b614540b958cb04cd931e37205628b"
      }
    }
  ],
  "repositoryName": "SdaLibraryProject"
}

```

### Endpoints:

#### `GET /api/github/repositories`

- **Parameters:**
    - `username` (query parameter) - GitHub username.
    - `Accept` (request header) - Should be set to "application/json".

- **Response:**
    - Returns a list of GitHubDTOs containing information about repositories.

- **Error Handling:**
    - If the `Accept` header is not set to "application/json," it returns a `Bad Request` response.
    - If the `username` header is not valid  it returns a `Not Found` response.

## Configuration

The application uses Spring `@Value` annotations for configuration. The following properties are defined:
- `github.api.url.users`: GitHub API URL for user information.
- `github.api.url.repos`: GitHub API URL for repository information.
- `github.token`: your githubToken is needed for test to run

## Usage

To use this application, make sure to configure the GitHub API properties and provide a valid GitHub API token.

### Building and Running the Application 

```bash
# Build the application
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```
### Building and Running the Application from Jar
```bash
# cmd/powershell
  java -jar .\AntiperaRecrutationTask-0.0.1-SNAPSHOT.jar
  #bash
  java -jar ./AntiperaRecrutationTask-0.0.1-SNAPSHOT.jar

```
### In current version you are able to make 60 request per hour

