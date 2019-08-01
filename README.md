# JStarCraft Example

****

希望路过的同学,顺手给JStarCraft框架点个Star,算是对作者的一种鼓励吧!

## 目录

* [介绍](#介绍)
* [特性](#特性)
    * [个性化推荐](#个性化推荐)
    * [个性化搜索](#个性化搜索)
* [安装](#安装)
    * [安装JStarCraft Core框架](#安装JStarCraft-Core框架)
    * [安装JStarCraft AI框架](#安装JStarCraft-AI框架)
    * [安装JStarCraft RNS引擎](#安装JStarCraft-RNS引擎)
    * [打包JStarCraft Example项目](#打包JStarCraft-Example项目)
* [使用](#使用)
    * [运行JStarCraft Example项目](#运行JStarCraft-Example项目)
    * [使用个性化推荐](#使用个性化推荐)
    * [使用个性化搜索](#使用个性化搜索)
* [架构](#架构)
* [概念](#概念)
* [示例](#示例)
* [对比](#对比)
* [版本](#版本)
* [参考](#参考)
* [协议](#协议)
* [作者](#作者)
* [致谢](#致谢)

****

## 介绍

JStarCraft Example是一个基于[JStarCraft RNS引擎](https://github.com/HongZhaoHua/jstarcraft-rns),Spring Boot框架和[MovieLens 100K数据集](https://grouplens.org/datasets/movielens/100k/)搭建的电影演示项目,包括**个性化推荐**与**个性化搜索**两个部分.

系统会根据用户的行为记录,自动调整用户的推荐内容和搜索内容.

****

## 特性

#### 个性化推荐

本演示项目使用的推荐算法涵盖**基准算法**,**协同算法**与**内容算法**3个方面:

基准算法
* AssociationRule
* MostPopular
* Random

协同算法
* BPR
* ItemKNN
* LDA
* UserKNN
* WRMF

内容算法

#### 个性化搜索

* 词项查询
* 范围查询
* 前缀查询
* 通配符查询
* 正则查询
* 模糊查询
* 组合查询

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
java -jar jstarcraf-example-1.0.jar
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

****

## 架构

****

## 概念

****

## 示例

****

## 对比

****

## 版本

****

## 参考

****

## 协议

****

## 作者

****

## 致谢

****
