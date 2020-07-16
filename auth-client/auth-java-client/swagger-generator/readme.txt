https://github.com/swagger-api/swagger-codegen
https://github.com/swagger-api/swagger-codegen#to-generate-a-sample-client-library

https://raw.githubusercontent.com/swagger-api/swagger-codegen/master/modules/swagger-codegen-maven-plugin/examples/java-client.xml

# 通过jar包生成
java -jar swagger-codegen-cli-2.2.2.jar help
java -jar swagger-codegen-cli-2.2.2.jar config-help -l java


# 通过maven插件生成
A Maven plugin to support the swagger code generation project
https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen-maven-plugin/README.md


# 在线生成方法
 * 打开页面 https://editor.swagger.io/
 * 复制swagger.json的api定义，输入到swagger editor中
 * 点击generate client，选择java语言
   * 在项目中更改pom.xml文件的项目配置groupId: com.ppdai.auth, artifactId: pauth-client
   * 更新client代码的package为com.ppdai.pauth
   * 更新readme文件