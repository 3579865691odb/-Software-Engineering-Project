# 乐器购物管理系统 Vue3

## 一、项目简介

乐器购物管理系统是一个基于 Vue3 前端和 Java 后端开发的在线购物管理系统，主要用于实现乐器商品的展示、购买、订单处理以及后台管理等功能。

本项目采用前后端分离架构，前端主要负责页面展示和用户交互，后端主要负责业务逻辑处理、数据库操作和接口服务。系统分为普通用户端和后台管理端，普通用户可以浏览商品、登录系统、提交订单，管理员可以对商品、用户、订单等信息进行管理。

本项目适合作为软件工程课程设计、Java Web 项目实践、Vue3 前后端分离项目学习使用。

---

## 二、项目目录结构

```text
乐器购物管理系统vue3/
├─ 代码/
│  ├─ server/              # 后端服务代码
│  └─ web/                 # 前端页面代码
│
├─ 数据库脚本/              # 数据库初始化 SQL 文件
│
└─ 文档/                    # 项目论文、需求分析、接口文档等
```

其中主要目录说明如下：

```text
代码/server/      后端服务目录，使用 Java 后端框架开发
代码/web/         前端项目目录，使用 Vue3 开发
数据库脚本/        存放数据库建表及初始化数据 SQL 文件
文档/              存放项目相关文档，如需求分析、设计文档、接口文档、论文等
```

---

## 三、技术栈

### 1. 前端技术

```text
Vue3
Vite
JavaScript
Element Plus
Axios
Vue Router
```

### 2. 后端技术

```text
Java
Spring Boot
Maven
MyBatis / MyBatis-Plus
MySQL
```

### 3. 开发工具

```text
IntelliJ IDEA
Visual Studio Code
MySQL
Navicat
Git
```

---

## 四、系统功能介绍

### 1. 普通用户端功能

普通用户登录系统后，可以使用以下功能：

```text
用户登录
浏览乐器商品
查看商品详情
购买商品
提交订单
查看个人订单
退出登录
```

### 2. 后台管理端功能

管理员登录后台后，可以使用以下功能：

```text
用户管理
商品管理
商品分类管理
订单管理
系统数据维护
```

---

## 五、运行环境要求

在运行本项目之前，请确保本机已经安装以下环境。

### 1. 前端环境

```text
Node.js：建议 16.x 或以上版本
npm：建议 8.x 或以上版本
```

可以使用以下命令查看版本：

```bash
node -v
npm -v
```

### 2. 后端环境

```text
JDK：1.8
Maven：3.6 或以上版本
MySQL：5.7 或 8.0
```

可以使用以下命令查看版本：

```bash
java -version
mvn -v
```

---

## 六、数据库配置

### 1. 创建数据库

首先在 MySQL 中创建项目数据库，例如：

```sql
CREATE DATABASE instrument_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

数据库名称可以根据项目实际配置进行修改。

### 2. 导入数据库脚本

数据库初始化脚本位于：

```text
乐器购物管理系统vue3/数据库脚本/
```

可以使用 Navicat、DataGrip 或 MySQL 命令行工具导入 SQL 文件。

使用 Navicat 导入的基本步骤如下：

```text
1. 打开 Navicat
2. 连接本地 MySQL 数据库
3. 新建数据库
4. 右键数据库，选择“运行 SQL 文件”
5. 选择项目中的数据库脚本文件
6. 点击开始导入
```

### 3. 修改后端数据库配置

进入后端项目目录：

```text
乐器购物管理系统vue3/代码/server/
```

找到配置文件，一般位于：

```text
src/main/resources/application.yml
```

或：

```text
src/main/resources/application.properties
```

根据自己的 MySQL 配置修改数据库连接信息。

示例配置如下：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/instrument_shop?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码
```

需要重点检查以下内容：

```text
数据库名称是否正确
MySQL 用户名是否正确
MySQL 密码是否正确
MySQL 服务是否已经启动
```

---

## 七、后端启动方式

### 1. 使用 IntelliJ IDEA 打开后端项目

打开以下目录：

```text
乐器购物管理系统vue3/代码/server/
```

等待 IDEA 自动识别 Maven 项目并下载依赖。

如果依赖没有自动下载，可以在 IDEA 右侧 Maven 面板中点击刷新按钮。

### 2. 检查 Maven 配置

如果 Maven 依赖下载失败，可以检查 IDEA 中的 Maven 配置：

```text
File → Settings → Build, Execution, Deployment → Build Tools → Maven
```

需要检查以下内容：

```text
Maven home path 是否正确
User settings file 是否存在
Local repository 是否正确
```

如果出现 settings.xml 不存在的错误，需要重新选择正确的 Maven 配置文件路径。

### 3. 启动后端服务

在后端项目中找到 Spring Boot 启动类，通常名称类似：

```text
Application.java
```

或：

```text
ServerApplication.java
```

右键选择：

```text
Run
```

后端启动成功后，控制台一般会显示类似信息：

```text
Tomcat started on port 8080
Started Application in ...
```

默认后端访问地址一般为：

```text
http://localhost:8080
```

具体端口以项目配置文件为准。

---

## 八、前端启动方式

### 1. 使用 VS Code 打开前端目录

打开以下目录：

```text
乐器购物管理系统vue3/代码/web/
```

### 2. 安装前端依赖

在 VS Code 终端中执行：

```bash
npm install
```

如果下载速度较慢，可以使用国内镜像：

```bash
npm install --registry=https://registry.npmmirror.com
```

### 3. 启动前端开发服务器

执行以下命令启动前端项目：

```bash
npm run dev
```

启动成功后，控制台会输出访问地址，例如：

```text
http://localhost:3000/
```

或者：

```text
http://localhost:5173/
```

在浏览器中打开控制台输出的地址即可访问系统。

---

## 九、系统登录账号

### 1. 普通用户账号

```text
账号：user
密码：123456
```

### 2. 后台管理员账号

```text
账号：admin
密码：123456
```

---

## 十、项目启动顺序

建议按照以下顺序启动项目：

```text
1. 启动 MySQL 数据库
2. 创建数据库
3. 导入数据库脚本
4. 修改后端数据库连接配置
5. 使用 IntelliJ IDEA 启动后端 server 服务
6. 使用 VS Code 启动前端 web 服务
7. 在浏览器中访问前端页面
```

需要注意的是，前端项目依赖后端接口服务。如果后端没有启动，前端页面可能可以正常打开，但登录、商品查询、订单提交等功能可能无法正常使用。

---

## 十一、常见问题说明

### 1. npm install 失败

可以先清除 npm 缓存：

```bash
npm cache clean --force
```

然后重新安装依赖：

```bash
npm install
```

如果下载速度较慢，可以使用国内镜像：

```bash
npm install --registry=https://registry.npmmirror.com
```

---

### 2. npm run dev 启动失败

可以检查以下内容：

```text
Node.js 是否安装成功
npm 是否安装成功
是否已经进入 web 前端目录
package.json 是否存在
依赖是否已经安装完成
```

可以重新执行：

```bash
npm install
npm run dev
```

---

### 3. 前端页面能打开，但是登录失败

可能原因如下：

```text
后端服务没有启动
数据库没有启动
数据库脚本没有正确导入
后端数据库连接配置错误
前端请求地址与后端端口不一致
账号或密码输入错误
```

解决方法：

```text
1. 检查 MySQL 是否运行
2. 检查后端 server 是否启动成功
3. 检查 application.yml 或 application.properties 配置
4. 检查数据库中是否存在用户数据
5. 检查浏览器控制台是否有接口请求错误
```

---

### 4. 后端启动失败

可能原因如下：

```text
JDK 版本不正确
Maven 依赖没有下载完整
数据库连接失败
端口号被占用
配置文件错误
```

如果是端口号被占用，可以修改后端配置文件中的端口号，例如：

```yaml
server:
  port: 8081
```

然后重新启动后端服务。

---

### 5. Maven 报 settings.xml 不存在

如果出现类似错误：

```text
The specified user settings file does not exist
```

说明 IDEA 中 Maven 的 settings.xml 路径配置错误。

可以进入：

```text
File → Settings → Build, Execution, Deployment → Build Tools → Maven
```

重新选择正确的 Maven 配置文件。

如果不确定 Maven 配置文件在哪里，可以使用 IDEA 自带 Maven，或者重新下载 Maven 后选择其 conf 目录下的 settings.xml 文件。

---

### 6. 数据库连接失败

如果后端控制台出现数据库连接失败，可以检查：

```text
MySQL 是否启动
数据库名称是否正确
用户名是否正确
密码是否正确
端口号是否正确
数据库脚本是否已经导入
```

常见 MySQL 默认端口为：

```text
3306
```

---

## 十二、GitHub 上传说明

如果需要将项目上传到 GitHub，可以在项目根目录执行以下命令：

```bash
git init
git add .
git commit -m "first commit"
git branch -M main
git remote add origin 你的GitHub仓库地址
git push -u origin main
```

例如：

```bash
git remote add origin https://github.com/用户名/仓库名.git
```

后续修改代码后，可以使用以下命令继续上传：

```bash
git add .
git commit -m "更新项目代码"
git push
```

建议在项目根目录添加 `.gitignore` 文件，避免上传无用文件。

示例 `.gitignore` 内容如下：

```gitignore
# Java
target/
*.class
*.jar
*.war

# Maven
.mvn/wrapper/maven-wrapper.jar

# Vue / Node
node_modules/
dist/

# IDEA
.idea/
*.iml

# System
.DS_Store
Thumbs.db
```

---

## 十三、项目说明

本项目主要用于软件工程课程设计，完整展示了一个前后端分离 Web 系统的基本开发流程，包括需求分析、数据库设计、后端接口开发、前端页面实现、用户登录、商品管理和订单管理等内容。

通过本项目可以学习以下内容：

```text
Vue3 前端项目开发
Spring Boot 后端项目开发
MySQL 数据库设计
前后端接口交互
用户登录与权限管理
商品与订单业务处理
Git 与 GitHub 项目管理
```

---

## 十四、项目基本信息

```text
项目名称：乐器购物管理系统 Vue3
项目类型：软件工程课程设计
前端目录：代码/web
后端目录：代码/server
数据库脚本目录：数据库脚本
项目文档目录：文档
```

---

## 十五、使用说明总结

```text
1. 进入数据库脚本目录，导入 SQL 文件
2. 进入代码/server，修改数据库连接配置
3. 使用 IntelliJ IDEA 启动后端服务
4. 进入代码/web，执行 npm install
5. 执行 npm run dev 启动前端服务
6. 浏览器访问前端控制台输出的地址
7. 使用 user/123456 登录用户端
8. 使用 admin/123456 登录后台管理端
```

---

## 十六、默认账号

```text
普通用户：
账号：user
密码：123456

后台管理员：
账号：admin
密码：123456
```

---

## 十七、备注

本项目仅用于课程设计、学习交流和项目实践，不建议直接用于正式商业环境。如需正式部署，请进一步完善权限控制、异常处理、数据校验、接口安全、日志管理和系统测试等内容。
