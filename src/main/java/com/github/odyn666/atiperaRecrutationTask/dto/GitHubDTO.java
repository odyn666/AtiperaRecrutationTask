package com.github.odyn666.atiperaRecrutationTask.dto;

import com.github.odyn666.atiperaRecrutationTask.model.BranchModel;
import lombok.Builder;

import java.util.List;


@Builder
public record GitHubDTO(String RepositoryName,
         String ownerLogin,
         List<BranchModel> branch) {
}
