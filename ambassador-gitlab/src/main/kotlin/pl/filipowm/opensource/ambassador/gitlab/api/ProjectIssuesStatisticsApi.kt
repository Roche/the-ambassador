package pl.filipowm.opensource.ambassador.gitlab.api

import org.gitlab4j.api.AbstractApi
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.GitLabApiException
import org.gitlab4j.api.models.IssuesStatistics
import org.gitlab4j.api.models.IssuesStatisticsFilter
import javax.ws.rs.core.Response

class ProjectIssuesStatisticsApi(gitLabApi: GitLabApi?) : AbstractApi(gitLabApi) {

    @Throws(GitLabApiException::class)
    fun getProjectIssuesStatistics(projectIdOrPath: Any?, filter: IssuesStatisticsFilter): IssuesStatistics {
        val formData = filter.queryParams
        val response: Response = get(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "issues_statistics")
        return response.readEntity(IssuesStatistics::class.java)
    }
}
