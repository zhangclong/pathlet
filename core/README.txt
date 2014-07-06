项目介绍
=============
Pathlet 核心工程，提供Pathlet IOC框架的核心代码。
这个工程提供了下列功能：
Pathlet IOC 的配置和加载核心。
Pathlet AOP 机制，目前仅提供了最常用的round point cut模式。
Pathlet Module 模块加载机制。

目录机构
===============
src 工程源码。
src-test 单元测试源码
target为编译目标路径。

如何构建
================
工程采用maven进行管理和构建（如对maven了解请参考主站 http://maven.apache.org）
本工程的pom.xml配置会依赖上级目录的pom.xml，请保持文件目录相关路径位置。
常用编译命令：
1. 在本机编译和部署执行命令：mvn clean install 
2. 在编译、打包并部署到远程maven repository上：mvn clean deploy 

