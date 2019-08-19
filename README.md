# JStarCraft Example

****

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Total lines](https://tokei.rs/b1/github/HongZhaoHua/jstarcraft-example?category=lines)](https://tokei.rs/b1/github/HongZhaoHua/jstarcraft-example?category=lines)

希望路过的同学,顺手给JStarCraft框架点个Star,算是对作者的一种鼓励吧!

## 目录

* [介绍](#介绍)
* [特性](#特性)
    * [个性化算法](#个性化算法)
* [安装](#安装)
    * [安装JStarCraft Core框架](#安装JStarCraft-Core框架)
    * [安装JStarCraft AI框架](#安装JStarCraft-AI框架)
    * [安装JStarCraft RNS引擎](#安装JStarCraft-RNS引擎)
    * [打包JStarCraft Example项目](#打包JStarCraft-Example项目)
* [使用](#使用)
    * [运行JStarCraft Example项目](#运行JStarCraft-Example项目)
    * [使用个性化推荐](#使用个性化推荐)
    * [使用个性化搜索](#使用个性化搜索)
    * [根据行为记录自动调整模型](#根据行为记录自动调整模型)
* [架构](#架构)
* [概念](#概念)
* [示例](#示例)
    * [词项查询](#词项查询)
    * [范围查询](#范围查询)
    * [通配符查询](#通配符查询)
    * [模糊查询](#模糊查询)
    * [组合查询](#组合查询)
* [对比](#对比)
* [版本](#版本)
* [参考](#参考)
* [协议](#协议)
* [作者](#作者)
* [致谢](#致谢)

****

## 介绍

JStarCraft Example是一个基于[JStarCraft RNS引擎](https://github.com/HongZhaoHua/jstarcraft-rns)引擎,Spring Boot框架和公共数据集搭建的千人千面演示项目.

系统会根据用户的行为记录,自动调整用户的推荐内容和搜索内容.使用者可以通过该项目了解**推荐系统**与**搜索系统**的运作流程.

涵盖了个性化推荐与个性化搜索2个部分.

****

## 特性

#### 个性化算法

本演示项目使用了以下8中个性化算法:
* AssociationRule
* MostPopular
* Random
* BPR
* ItemKNN
* LDA
* UserKNN
* WRMF

[点击了解更多的个性化算法](https://github.com/HongZhaoHua/jstarcraft-rns#%E4%B8%AA%E6%80%A7%E5%8C%96%E7%AE%97%E6%B3%95)

****

## 安装

项目为了尽可能聚焦于个性化推荐和个性化搜索的演示,不包含任何多余组件的部署(例如MySQL/Redis/Spark/Elasticsearch).

JStarCraft Examlpe要求使用者具备以下环境:
* JDK 8或者以上
* Maven 3

#### 安装JStarCraft-Core框架

```shell
git clone https://github.com/HongZhaoHua/jstarcraft-core.git

mvn install -Dmaven.test.skip=true
```

#### 安装JStarCraft-AI框架

```shell
git clone https://github.com/HongZhaoHua/jstarcraft-ai.git

mvn install -Dmaven.test.skip=true
```

####  安装JStarCraft-RNS引擎

```shell
git clone https://github.com/HongZhaoHua/jstarcraft-rns.git

mvn install -Dmaven.test.skip=true
```

#### 打包JStarCraft-Example项目

```shell
git clone https://github.com/HongZhaoHua/jstarcraft-example.git

mvn package -Dmaven.test.skip=true
```

****

## 使用

#### 运行JStarCraft-Example项目

```shell
java -jar jstarcraft-example-1.0.jar
```

#### 使用个性化推荐

1. 复制链接[http://127.0.0.1:8080?type=recommend](http://127.0.0.1:8080?type=recommend)到浏览器.
2. 选择用户
3. 选择推荐算法
4. 点击推荐

推荐效果如图所示:
![recommend](https://github.com/HongZhaoHua/jstarcraft-example/blob/master/recommend.png)

#### 使用个性化搜索

1. 复制链接[http://127.0.0.1:8080?type=search](http://127.0.0.1:8080?type=search)到浏览器.
2. 选择用户
3. 填写搜索内容
4. 点击搜索

搜索效果如图所示:
![search](https://github.com/HongZhaoHua/jstarcraft-example/blob/master/search.png)

#### 根据行为记录自动调整模型

用户可以通过点击对应的分数给电影打分,系统每隔5分钟会自动刷新模型.

****

## 架构

****

## 概念

****

## 示例

#### 词项查询

支持单词和语句:
- 单词
- 语句(使用双引号`""`包括)

```
Story
"Toy Story"
```

#### 范围查询

支持指定最小值和最大值:
- {}尖括号表示不包含最小值或最大值
- []方括号表示包含最小值或最大值

```
[1990,2000}
```

#### 通配符查询

支持在单词或者语句中结合通配符:
- 使用`?`匹配单个字符
- 使用`*`匹配0个或多个字符

```
te?t
te*t
```

#### 模糊查询

```
test~
```

#### 组合查询

支持多种逻辑操作符:
- 使用`&&`实现交集操作
- 使用`||`实现并集操作
- 使用`!`实现差集操作
- 使用`()`实现分组操作,形成更为复杂的逻辑查询

```
1990 || (Toy && Story)
```

****

## 对比

****

## 版本

****

## 参考

****

## 协议

JStarCraft Example遵循[Apache 2.0协议](https://www.apache.org/licenses/LICENSE-2.0.html),一切以其为基础的衍生作品均属于衍生作品的作者.

****

## 作者

| 作者 | 洪钊桦 |
| :----: | :----: |
| E-mail | 110399057@qq.com, jstarcraft@gmail.com |

****

## 致谢

特别感谢[LibRec团队](https://github.com/guoguibing/librec)在推荐方面提供的支持与帮助.

特别感谢[陆徐刚](https://github.com/luxugang/Lucene-7.5.0)在搜索方面提供的支持与帮助.

****
