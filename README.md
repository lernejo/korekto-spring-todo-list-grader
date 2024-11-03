# korekto-spring-todo-list-grader
[![Build](https://github.com/lernejo/korekto-spring-todo-list-grader/actions/workflows/ci.yml/badge.svg)](https://github.com/lernejo/korekto-spring-todo-list-grader/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/lernejo/korekto-spring-todo-list-grader/branch/main/graph/badge.svg?token=A6kYtPT5DX)](https://codecov.io/gh/lernejo/korekto-spring-todo-list-grader)
![License](https://img.shields.io/badge/License-Elastic_License_v2-blue)

Korekto grader & exercise about using Spring-Boot & Postgres to create a simple web API.

Exercise subject: [here](EXERCISE_fr.adoc)

# How to launch
You will need these 2 env vars:
* `GH_LOGIN` your GitHub login
* `GH_TOKEN` a [**P**ersonal **A**ccess **T**oken](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic) with permissions `repo:read` and `user:read`

```bash
git clone git@github.com:lernejo/korekto-spring-todo-list-grader.git
cd korekto-spring-todo-list-grader
mvn compile exec:java -Dexec.args="-s=$GH_LOGIN" -Dgithub_token="$GH_TOKEN"
```

### With intelliJ

![Demo Run Configuration](https://raw.githubusercontent.com/lernejo/korekto-toolkit/main/docs/demo_run_configuration.png)
