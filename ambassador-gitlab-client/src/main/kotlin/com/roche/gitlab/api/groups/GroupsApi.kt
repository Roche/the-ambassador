package com.roche.gitlab.api.groups

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient

class GroupsApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client)
