<h4 align="right"><strong><a href="jeg-docs/README_EN.md">English</a></strong> | 中文 </h4>
<p align="center">
  <h1 align="center">Java Echo Generator</h1>
  <div align="center">
    <img alt="GitHub watchers" src="https://img.shields.io/github/watchers/pen4uin/java-echo-generator?style=flat-square">
    <img alt="GitHub forks" src="https://img.shields.io/github/forks/pen4uin/java-echo-generator?style=flat-square">
    <img alt="GitLab Stars" src="https://img.shields.io/github/stars/pen4uin/java-echo-generator.svg?style=flat-square">
  </div>
  <div align="center">一款支持高度自定义的 Java 回显载荷生成工具</div>
</p>

<br>

> [!WARNING]
> 本工具仅供安全研究和学习使用。使用者需自行承担因使用此工具产生的所有法律及相关责任。请确保你的行为符合当地的法律和规定。作者不承担任何责任。如不接受，请勿使用此工具。

<br>

## 功能

| 中间件       | 框架        | 执行模式    | 输出格式       | 
|-----------|-----------|---------|------------|
| Tomcat    | SpringMVC | Command | BASE64     | 
| Resin     | Struts2   | Code    | BCEL       | 
| WebLogic  |           |         | BIGINTEGER | 
| Jetty     |           |         | CLASS      | 
| WebSphere |           |         | JAR        | 
| Undertow  |           |         | JS         |
| GlassFish |           |         |            | 


## 编译

```shell
mvn package assembly:single
```

## 使用

**图形化**

1. 下载 jEG-GUI-1.0.0.jar 运行即可

![image-20230928161217950](./jeg-docs/img/gui.png)

**Woodpecker 插件**

1. 下载 jEG-Woodpecker-1.0.0.jar 到 woodpecker 插件目录下即可

![image-20230928153330494](./jeg-docs/img/woodpecker-plugin.png)

**第三方库**

1. 下载 jEG-Core-1.0.0.jar 并安装到本地 maven 仓库

```
mvn install:install-file -Dfile=jEG-Core-1.0.0.jar -DgroupId=jeg -DartifactId=jeg-core -Dversion=1.0.0 -Dpackaging=jar
```

2. 引入自己的框架/工具的依赖中

```
<dependency>
    <groupId>jeg</groupId>
    <artifactId>jeg-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

3. 调用 API 生成需要的回显载荷即可

```
// 基本配置
jEGConfig config = new jEGConfig() {{
    // 设置待回显的中间件为 tomcat
    setServerType(Constants.SERVER_TOMCAT);
    // 设置待执行的 payload 为命令执行回显
    setModelType(Constants.MODEL_CMD);
    // 设置 payload 的输出格式为 BASE64
    setFormatType(Constants.FORMAT_BASE64);
    // 初始化基础配置
    build();
}};
// 生成 payload
jEGenerator generator = new jEGenerator(config);
System.out.println("请求头: " + config.getReqHeaderName());
System.out.println(generator.getPayload());
```

## 文档

- [jEG v1.0.0 - 高度自定义的 Java 回显生成工具](./jeg-docs/1.0.0/)

## 致谢

- https://gv7.me/articles/2020/semi-automatic-mining-request-implements-multiple-middleware-echo/
- https://gist.github.com/fnmsd/8165cedd9fe735d7ef438b2e977af327
- https://github.com/feihong-cs/Java-Rce-Echo

## 协议

- MIT