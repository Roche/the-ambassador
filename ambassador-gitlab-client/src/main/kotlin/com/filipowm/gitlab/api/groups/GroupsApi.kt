package com.filipowm.gitlab.api.groups

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.client.GitLabHttpClient

class GroupsApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client)
