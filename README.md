# GitHub Reports Tool

Toy program to fetch GH all repos from certain org.

## 🛠 Built with

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![OpenJDK](https://img.shields.io/badge/OpenJDK-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![EditorConfig](https://img.shields.io/badge/Editor%20Config-E0EFEF?style=for-the-badge&logo=editorconfig&logoColor=000)
![GitHub API](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![IntelliJ](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

## 🏃‍♀️ Running

1. Install SDKMan!.
   ```shell
   curl -s "https://get.sdkman.io" | bash
   ```
1. Source it.
   ```shell
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```
1. Install JDK and Maven.
   ```shell
   sdk env
   ```
1. Compile the slim JDK
   ```shell
   mvn compile jlink:jlink
   ```
1. Run it, setting the env var `GITHUB_TOKEN` with
   your [PAT](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
   .
   ```shell
   GITHUB_TOKEN=<PAT> ./target/maven-jlink/default/bin/gh-reporter --org <GH_ORG>
   ```

## Options

### content and filename

Uses GH API to search for the content. Should `filename` be set then the searching will be narrowed down to that file in
each GH repo.

```shell
   GITHUB_TOKEN=<PAT> ./target/maven-jlink/default/bin/gh-reporter \
   --org <GH_ORG> \
   --content "license MIT" \
   --filename package.json
```

Would be roughly equivalent to:

```shell
  curl -H "Authorization: Bearer <PAT>" \
  -H "Accept: application/vnd.github.text-match+json" \
  "https://api.github.com/search/code?q=license+mit+filename%3Apackage.json+in%3Afile+org%3A<GH_ORG>"
```
