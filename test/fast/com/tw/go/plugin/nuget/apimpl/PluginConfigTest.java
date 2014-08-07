package com.tw.go.plugin.nuget.apimpl;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProperty;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.tw.go.plugin.nuget.config.NuGetPackageConfig;
import com.tw.go.plugin.nuget.config.NuGetRepoConfig;
import com.tw.go.plugin.util.RepoUrl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.thoughtworks.go.plugin.api.config.Property.*;
import static com.tw.go.plugin.nuget.config.NuGetPackageConfig.*;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PluginConfigTest {
    private PluginConfig pluginConfig;

    @Before
    public void setUp() {
        pluginConfig = new PluginConfig();
    }

    @Test
    public void shouldGetRepositoryConfiguration() {
        RepositoryConfiguration configurations = pluginConfig.getRepositoryConfiguration();
        assertThat(configurations.get(RepoUrl.REPO_URL), is(notNullValue()));
        assertThat(configurations.get(RepoUrl.REPO_URL).getOption(SECURE), is(false));
        assertThat(configurations.get(RepoUrl.REPO_URL).getOption(REQUIRED), is(true));
        assertThat(configurations.get(RepoUrl.REPO_URL).getOption(DISPLAY_NAME), is("NuGet server API root"));
        assertThat(configurations.get(RepoUrl.REPO_URL).getOption(DISPLAY_ORDER), is(0));
        assertThat(configurations.get(RepoUrl.USERNAME), is(notNullValue()));
        assertThat(configurations.get(RepoUrl.USERNAME).getOption(SECURE), is(false));
        assertThat(configurations.get(RepoUrl.USERNAME).getOption(REQUIRED), is(false));
        assertThat(configurations.get(RepoUrl.USERNAME).getOption(DISPLAY_NAME), is("UserName"));
        assertThat(configurations.get(RepoUrl.USERNAME).getOption(DISPLAY_ORDER), is(1));
        assertThat(configurations.get(RepoUrl.PASSWORD), is(notNullValue()));
        assertThat(configurations.get(RepoUrl.PASSWORD).getOption(SECURE), is(true));
        assertThat(configurations.get(RepoUrl.PASSWORD).getOption(REQUIRED), is(false));
        assertThat(configurations.get(RepoUrl.PASSWORD).getOption(DISPLAY_NAME), is("Password"));
        assertThat(configurations.get(RepoUrl.PASSWORD).getOption(DISPLAY_ORDER), is(2));
    }

    @Test
    public void shouldGetPackageConfiguration() {
        PackageConfiguration configurations = pluginConfig.getPackageConfiguration();
        assertNotNull(configurations.get(PACKAGE_ID));
        assertThat(configurations.get(PACKAGE_ID).getOption(DISPLAY_NAME), is("Package Id"));
        assertThat(configurations.get(PACKAGE_ID).getOption(DISPLAY_ORDER), is(0));
        assertThat(configurations.get(PACKAGE_ID).getOption(PART_OF_IDENTITY), is(true));
        assertNotNull(configurations.get(POLL_VERSION_FROM));
        assertThat(configurations.get(POLL_VERSION_FROM).getOption(DISPLAY_NAME), is("Version to poll >="));
        assertThat(configurations.get(POLL_VERSION_FROM).getOption(DISPLAY_ORDER), is(1));
        assertThat(configurations.get(POLL_VERSION_FROM).getOption(REQUIRED), is(false));
        assertThat(configurations.get(POLL_VERSION_FROM).getOption(PART_OF_IDENTITY), is(true));
        assertNotNull(configurations.get(POLL_VERSION_TO));
        assertThat(configurations.get(POLL_VERSION_TO).getOption(DISPLAY_NAME), is("Version to poll <"));
        assertThat(configurations.get(POLL_VERSION_TO).getOption(DISPLAY_ORDER), is(2));
        assertThat(configurations.get(POLL_VERSION_TO).getOption(REQUIRED), is(false));
        assertThat(configurations.get(POLL_VERSION_TO).getOption(PART_OF_IDENTITY), is(true));
        assertThat(configurations.get(INCLUDE_PRE_RELEASE).getOption(PART_OF_IDENTITY), is(true));
    }

    @Test
    public void shouldValidateRepoUrl() {
        assertForRepositoryConfigurationErrors(new RepositoryConfiguration(), asList(new ValidationError(RepoUrl.REPO_URL, "Repository url not specified")), false);
        assertForRepositoryConfigurationErrors(repoConfigurations(RepoUrl.REPO_URL, null), asList(new ValidationError(RepoUrl.REPO_URL, "Repository url not specified")), false);
        assertForRepositoryConfigurationErrors(repoConfigurations(RepoUrl.REPO_URL, ""), asList(new ValidationError(RepoUrl.REPO_URL, "Repository url not specified")), false);
        assertForRepositoryConfigurationErrors(repoConfigurations(RepoUrl.REPO_URL, "incorrectUrl"), asList(new ValidationError(RepoUrl.REPO_URL, "Only http/https urls are supported")), false);
        assertForRepositoryConfigurationErrors(repoConfigurations(RepoUrl.REPO_URL, "http://correct.com/url"), new ArrayList<ValidationError>(), true);
    }

    @Test
    public void shouldRejectUnsupportedTagsInRepoConfig() {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new PackageMaterialProperty(RepoUrl.REPO_URL, "http://nuget.org"));
        repoConfig.add(new PackageMaterialProperty("unsupported_key", "value"));
        assertForRepositoryConfigurationErrors(
                repoConfig,
                asList(new ValidationError("Unsupported key: unsupported_key. Valid keys: " + Arrays.toString(NuGetRepoConfig.getValidKeys()))),
                false);

    }

    @Test
    public void shouldRejectUnsupportedTagsInPkgConfig() {
        PackageConfiguration pkgConfig = new PackageConfiguration();
        pkgConfig.add(new PackageMaterialProperty(PACKAGE_ID, "abc"));
        pkgConfig.add(new PackageMaterialProperty("unsupported_key", "value"));
        assertForPackageConfigurationErrors(
                pkgConfig,
                asList(new ValidationError("Unsupported key: unsupported_key. Valid keys: " + Arrays.toString(NuGetPackageConfig.getValidKeys()))),
                false);
    }

    @Test
    public void shouldValidatePackageId() {
        assertForPackageConfigurationErrors(new PackageConfiguration(), asList(new ValidationError(PACKAGE_ID, "Package id not specified")), false);
        assertForPackageConfigurationErrors(configurations(PACKAGE_ID, null), asList(new ValidationError(PACKAGE_ID, "Package id is null")), false);
        assertForPackageConfigurationErrors(configurations(PACKAGE_ID, ""), asList(new ValidationError(PACKAGE_ID, "Package id is empty")), false);
        assertForPackageConfigurationErrors(configurations(PACKAGE_ID, "go-age?nt-*"), asList(new ValidationError(PACKAGE_ID, "Package id [go-age?nt-*] is invalid")), false);
        assertForPackageConfigurationErrors(configurations(PACKAGE_ID, "go-agent"), new ArrayList<ValidationError>(), true);
    }

    private void assertForRepositoryConfigurationErrors(RepositoryConfiguration repositoryConfigurations, List<ValidationError> expectedErrors, boolean expectedValidationResult) {
        ValidationResult errors = pluginConfig.isRepositoryConfigurationValid(repositoryConfigurations);
        assertThat(errors.isSuccessful(), is(expectedValidationResult));
        assertThat(errors.getErrors().size(), is(expectedErrors.size()));
        assertThat(errors.getErrors().containsAll(expectedErrors), is(true));
    }

    private void assertForPackageConfigurationErrors(PackageConfiguration packageConfiguration, List<ValidationError> expectedErrors, boolean expectedValidationResult) {
        final RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new PackageMaterialProperty(RepoUrl.REPO_URL, "http://nuget.org/v2"));
        ValidationResult errors = pluginConfig.isPackageConfigurationValid(packageConfiguration, repoConfig);
        assertThat(errors.isSuccessful(), is(expectedValidationResult));
        assertThat(errors.getErrors().size(), is(expectedErrors.size()));
        assertThat(errors.getErrors().containsAll(expectedErrors), is(true));
    }

    private PackageConfiguration configurations(String key, String value) {
        PackageConfiguration packageConfiguration = new PackageConfiguration();
        packageConfiguration.add(new PackageMaterialProperty(key, value));
        return packageConfiguration;
    }

    private RepositoryConfiguration repoConfigurations(String key, String value) {
        RepositoryConfiguration packageConfiguration = new RepositoryConfiguration();
        packageConfiguration.add(new PackageMaterialProperty(key, value));
        return packageConfiguration;
    }
}
