package com.github.odyn666.atiperaRecrutationTask.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GitHubRepositoryModel {
    String name;
    boolean fork;
    List<BranchModel>branches;
    GitHubUserModel owner;

}
