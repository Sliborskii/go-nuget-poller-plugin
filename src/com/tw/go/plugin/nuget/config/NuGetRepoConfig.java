package com.tw.go.plugin.nuget.config;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfigurations;
import com.tw.go.plugin.util.RepoUrl;

public class NuGetRepoConfig {
    private final PackageConfigurations repoConfigs;
    private final PackageConfiguration repoUrlConfig;

    public NuGetRepoConfig(PackageConfigurations repoConfigs) {
        this.repoConfigs = repoConfigs;
        repoUrlConfig = repoConfigs.get(RepoUrl.REPO_URL);
    }

    public String stringValueOf(PackageConfiguration packageConfiguration) {
        if (packageConfiguration == null) return null;
        return packageConfiguration.getValue();
    }

    public RepoUrl getRepoUrl() {
        return RepoUrl.create(
                repoUrlConfig.getValue(),
                stringValueOf(repoConfigs.get(RepoUrl.USERNAME)),
                stringValueOf(repoConfigs.get(RepoUrl.PASSWORD)));
    }
    public boolean isHttp() {
        return RepoUrl.create(
                repoUrlConfig.getValue(),
                stringValueOf(repoConfigs.get(RepoUrl.USERNAME)),
                stringValueOf(repoConfigs.get(RepoUrl.PASSWORD))).isHttp();
    }

    public boolean isRepoUrlMissing() {
        return repoUrlConfig == null;
    }

    public static String[] getValidKeys() {
        return new String[]{RepoUrl.REPO_URL, RepoUrl.USERNAME, RepoUrl.PASSWORD};
    }
}
