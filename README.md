# GitHub Reports Tool

## 🛠 Construído com

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![OpenJDK](https://img.shields.io/badge/OpenJDK-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![EditorConfig](https://img.shields.io/badge/Editor%20Config-E0EFEF?style=for-the-badge&logo=editorconfig&logoColor=000)
![GitHub API](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![IntelliJ](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

## ✍️ Desenvolvendo

Projeto utiliza JDK 19.

1. Instale o SDKMan!.
   ```shell
   curl -s "https://get.sdkman.io" | bash
   ```
1. Execute o script nesta sessão
   ```shell
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```
1. Instale o JDK e Maven.
   ```shell
   sdk env
   ```
1. Execute, definindo a variável de ambiente `GITHUB_TOKEN` com
   seu [PAT](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
   .
   ```shell
   GITHUB_TOKEN=<> mvn compile exec:java
   ```

## 📖Logging

No arquivo `pom.xml` substitua a seção abaixo para detalhar as requisições.

```xml

<systemProperty>
  <key>jdk.httpclient.HttpClient.log</key>
  <value>errors,requests,headers,
    frames[:control:data:window:all],content,ssl,trace,channel,all
  </value>
</systemProperty>
```
