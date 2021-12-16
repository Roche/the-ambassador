# Contributing

When contributing to this repository, please first discuss the change you wish to make via issue, email, or any
other method with the owners of this repository before making a change.
Please note we have a [code of conduct](CODE_OF_CONDUCT.md), please follow it in all your interactions with the project.

## Development environment setup

Prerequisites:

1. Install JDK 11 _(upgrade to JDK 17 is planned)_
2. [Install Docker](https://docs.docker.com/engine/install/)

To set up a development environment, please follow these steps:

1. Clone the repo

   ```sh
   git clone https://github.com/Roche/the-ambassador
   ```

2. Import project to your favorite IDE as `gradle` project

3. Start Postgres 13 database

   ```bash
   docker run -d -p 5432:5432 -v ambassador -e POSTGRES_PASSWORD=postgres --name ambassador postgres:14 
   ```

4. Configure local project source.
   - create `application-secrets.yml` under `ambassador-application/src/main/resources`
   - use one of these template for configuration:
     
     * `fake` source, which generates fake data:
       ```yaml
       ambassador:
         source:
           name: gitlab # currently name must be always set to this
           url: http://fake.com
           token: fake
           system: fake
       ```
       
     * `gitlab` source, connected to existing GitLab instance
       ```yaml
       ambassador:
         source:
           name: gitlab
           url: <gitlab_url>
           token: <persona_access_token>
           system: gitlab
           # clientId: <oauth2_client_id> # optional, not needed for local setup running without security
           # clientSecret: <oauth2_secret>
       ```
       For GitLab source you need to provide valid URL and [Personal Access Token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html).
   
5. [optional] To limit amount of data indexed, until you learn more about
   Ambassador, it is recommended to set up `ambassador.indexer.criteria.projects.groups`
   property to limit amount of data fetched to only few groups/organizations.
   
6. Start Ambassador either from your IDE or command line. \
   **Important!** Use profile `local` to run application locally.

   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```
   
You should be able to access Swagger UI at `localhost:8080/swagger-ui.html`.

## Issues and feature requests

You've found a bug in the source code, a mistake in the documentation or maybe you'd like a new feature? 
You can help us by [submitting an issue on GitHub](https://github.com/Roche/the-ambassador/issues). 
Before you create an issue, make sure to search the issue archive -- your issue may have already been addressed!

Please try to create bug reports that are:

- _Reproducible._ Include steps to reproduce the problem.
- _Specific._ Include as much detail as possible: which version, what environment, etc.
- _Unique._ Do not duplicate existing opened issues.
- _Scoped to a Single Bug._ One bug per report.

**Even better: Submit a pull request with a fix or new feature!**

### How to submit a Pull Request

1. Search our repository for open or closed
   [Pull Requests](https://github.com/Roche/the-ambassador/pulls)
   that relate to your submission. You don't want to duplicate effort.
2. Fork the project
3. Create your feature branch (`git checkout -b feat/amazing_feature`)
4. Commit your changes (`git commit -m 'feat: add amazing_feature'`) The Ambassador uses [conventional commits](https://www.conventionalcommits.org), 
   so please follow the specification in your commit messages.
5. Push to the branch (`git push origin feat/amazing_feature`)
6. [Open a Pull Request](https://github.com/Roche/the-ambassador/compare?expand=1)

## Project assistance

If you want to say **thank you** or/and support active development of The Ambassador:

- Add a [GitHub Star](https://github.com/Roche/the-ambassador) to the project.
- Tweet about the The Ambassador.
- Write interesting articles about the project on [Dev.to](https://dev.to/), [Medium](https://medium.com/) or your personal blog.

Together, we can make The Ambassador **better**!