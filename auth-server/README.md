README
===========================

## 准备
### 下载项目并切换到开发分支
git clone pauth

git checkout dev


## 项目构建

本项目为后端spring boot项目，技术栈为spring mvc + spring boot + spring jpa + mysql

请参考如下步骤对项目进行构建启动，

### 0. 安装Java/Maven

JDK 8+

### 1. 后端项目构建

构建文件：./pom.xml

构建命令：mvn compile package

### 2. 后端项目开发调试

根据不同IDE，请选择相应的开发调试选项

### 3. 后端项目运行

java -jar target\pauth-0.0.1-SNAPSHOT.jar