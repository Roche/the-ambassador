package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.filipowm.gitlab.api.model.Visibility
import com.filipowm.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class Project(
    @JsonProperty("allow_merge_on_skipped_pipeline")
    val allowMergeOnSkippedPipeline: Boolean? = null,
    @JsonProperty("approvals_before_merge")
    val approvalsBeforeMerge: Int? = null,
    @JsonProperty("archived")
    val archived: Boolean? = null,
    @JsonProperty("auto_devops_deploy_strategy")
    val autoDevopsDeployStrategy: String? = null,
    @JsonProperty("auto_devops_enabled")
    val autoDevopsEnabled: Boolean? = null,
    @JsonProperty("autoclose_referenced_issues")
    val autocloseReferencedIssues: Boolean? = null,
    @JsonProperty("avatar_url")
    override val avatarUrl: String? = null,
    @JsonProperty("can_create_merge_request_in")
    val canCreateMergeRequestIn: Boolean? = null,
    @JsonProperty("ci_default_git_depth")
    val ciDefaultGitDepth: Int? = null,
    @JsonProperty("ci_forward_deployment_enabled")
    val ciForwardDeploymentEnabled: Boolean? = null,
    @JsonProperty("compliance_frameworks")
    val complianceFrameworks: List<String>? = null,
    @JsonProperty("container_expiration_policy")
    val containerExpirationPolicy: ContainerExpirationPolicy? = null,
    @JsonProperty("container_registry_access_level")
    val containerRegistryAccessLevel: String? = null,
    @JsonProperty("container_registry_enabled")
    val containerRegistryEnabled: Boolean? = null,
    @JsonProperty("container_registry_image_prefix")
    val containerRegistryImagePrefix: String? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    override val createdAt: LocalDateTime? = null,
    @JsonProperty("creator_id")
    val creatorId: Int? = null,
    @JsonProperty("default_branch")
    override val defaultBranch: String? = null,
    @JsonProperty("description")
    override val description: String? = null,
    @JsonProperty("external_authorization_classification_label")
    val externalAuthorizationClassificationLabel: Any? = null,
    @JsonProperty("forks_count")
    override val forksCount: Int? = null,
    @JsonProperty("http_url_to_repo")
    override val httpUrlToRepo: String? = null,
    @JsonProperty("id")
    override val id: Int? = null,
    @JsonProperty("import_error")
    val importError: Any? = null,
    @JsonProperty("import_status")
    val importStatus: String? = null,
    @JsonProperty("issues_enabled")
    val issuesEnabled: Boolean? = null,
    @JsonProperty("jobs_enabled")
    val jobsEnabled: Boolean? = null,
    @JsonProperty("lfs_enabled")
    val lfsEnabled: Boolean? = null,
    @JsonProperty("last_activity_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    override val lastActivityAt: LocalDateTime? = null,
    @JsonProperty("license")
    val license: License? = null,
    @JsonProperty("license_url")
    val licenseUrl: String? = null,
    @JsonProperty("marked_for_deletion_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val markedForDeletionAt: LocalDateTime? = null,
    @JsonProperty("marked_for_deletion_on")
    val markedForDeletionOn: String? = null,
    @JsonProperty("merge_method")
    val mergeMethod: String? = null,
    @JsonProperty("merge_requests_enabled")
    val mergeRequestsEnabled: Boolean? = null,
    @JsonProperty("mirror")
    val mirror: Boolean? = null,
    @JsonProperty("mirror_overwrites_diverged_branches")
    val mirrorOverwritesDivergedBranches: Boolean? = null,
    @JsonProperty("mirror_trigger_builds")
    val mirrorTriggerBuilds: Boolean? = null,
    @JsonProperty("mirror_user_id")
    val mirrorUserId: Int? = null,
    @JsonProperty("name")
    override val name: String,
    @JsonProperty("name_with_namespace")
    override val nameWithNamespace: String? = null,
    @JsonProperty("namespace")
    override val namespace: Namespace? = null,
    @JsonProperty("only_allow_merge_if_all_discussions_are_resolved")
    val onlyAllowMergeIfAllDiscussionsAreResolved: Boolean? = null,
    @JsonProperty("only_allow_merge_if_pipeline_succeeds")
    val onlyAllowMergeIfPipelineSucceeds: Boolean? = null,
    @JsonProperty("only_mirror_protected_branches")
    val onlyMirrorProtectedBranches: Boolean? = null,
    @JsonProperty("open_issues_count")
    val openIssuesCount: Int? = null,
    @JsonProperty("owner")
    val owner: Owner? = null,
    @JsonProperty("packages_enabled")
    val packagesEnabled: Boolean? = null,
    @JsonProperty("path")
    override val path: String,
    @JsonProperty("path_with_namespace")
    override val pathWithNamespace: String? = null,
    @JsonProperty("permissions")
    val permissions: Permissions? = null,
    @JsonProperty("printing_merge_requests_link_enabled")
    val printingMergeRequestsLinkEnabled: Boolean? = null,
    @JsonProperty("public_jobs")
    val publicJobs: Boolean? = null,
    @JsonProperty("readme_url")
    override val readmeUrl: String? = null,
    @JsonProperty("remove_source_branch_after_merge")
    val removeSourceBranchAfterMerge: Boolean? = null,
    @JsonProperty("repository_storage")
    val repositoryStorage: String? = null,
    @JsonProperty("request_access_enabled")
    val requestAccessEnabled: Boolean? = null,
    @JsonProperty("resolve_outdated_diff_discussions")
    val resolveOutdatedDiffDiscussions: Boolean? = null,
    @JsonProperty("restrict_user_defined_valiables")
    val restrictUserDefinedvaliables: Boolean? = null,
    @JsonProperty("runners_token")
    val runnersToken: String? = null,
    @JsonProperty("service_desk_address")
    val serviceDeskAddress: Any? = null,
    @JsonProperty("service_desk_enabled")
    val serviceDeskEnabled: Boolean? = null,
    @JsonProperty("shared_runners_enabled")
    val sharedRunnersEnabled: Boolean? = null,
    @JsonProperty("shared_with_groups")
    val sharedWithGroups: List<SharedWithGroup>? = null,
    @JsonProperty("snippets_enabled")
    val snippetsEnabled: Boolean? = null,
    @JsonProperty("squash_option")
    val squashOption: String? = null,
    @JsonProperty("ssh_url_to_repo")
    override val sshUrlToRepo: String? = null,
    @JsonProperty("star_count")
    override val starCount: Int? = null,
    @JsonProperty("statistics")
    val statistics: Statistics? = null,
    @JsonProperty("suggestion_commit_message")
    val suggestionCommitMessage: Any? = null,
    @JsonProperty("tag_list")
    override val tagList: List<String>? = null,
    @JsonProperty("topics")
    override val topics: List<String>? = null,
    @JsonProperty("visibility")
    val visibility: Visibility? = null,
    @JsonProperty("web_url")
    override val webUrl: String? = null,
    @JsonProperty("wiki_enabled")
    val wikiEnabled: Boolean? = null,
    @JsonProperty("forked_from_project")
    val forkedFrom: SimpleProject? = null,

    ) : SimpleProject(
    avatarUrl, createdAt, defaultBranch, description, forksCount, httpUrlToRepo, id, lastActivityAt, name, nameWithNamespace, namespace, path, pathWithNamespace, readmeUrl,
    sshUrlToRepo, starCount, tagList, topics, webUrl
) {

    @JsonIgnore
    fun isForked(): Boolean = forkedFrom != null

    companion object {
        fun forCreate(name: String, path: String, namespaceId: Int? = null, description: String? = null): Project {
            val namespace = Namespace(id = namespaceId)
            return Project(name = name, path = path, description = description, namespace = namespace)
        }

        fun forCreate(name: String, pathNamingStrategy: PathNamingStrategy = PathNamingStrategy.default(), namespaceId: Int? = null, description: String? = null): Project {
            return forCreate(name = name, path = pathNamingStrategy.getPathFor(name), description = description, namespaceId = namespaceId)
        }
    }
}
