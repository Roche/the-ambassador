package pl.filipowm.gitlab.api

interface GitLab {

    fun projects(): Projects

    companion object {
        fun builder(): GitLabApiBuilder {
            return GitLabApiBuilder()
        }
    }

    enum class ApiVersion {
        V4
    }
}
