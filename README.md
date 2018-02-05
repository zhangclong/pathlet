Pathlet - Java IOC Framework in path way
=================================================
Pathlet框架是一个以惯例优先原则（convention over configuration）为核心设计理念的Java IOC框架；
先对Spring framework这个流行的IOC框架，Pathlet有其特有的先天优势：
1. 对树节点的动态加载和卸载。
2. 对树节点的批量操作。


如何构建
================
工程采用maven进行管理和构建（如对maven了解请参考主站 http://maven.apache.org）
本工程的pom.xml配置会依赖上级目录的pom.xml，请保持文件目录相关路径位置。
常用编译命令：
1. 在本机编译和部署执行命令：mvn clean install 
2. 在编译、打包并部署到远程maven repository上：mvn clean deploy 
3. 编译Parent POM跳过下面的子Module，mvn install -N
   


