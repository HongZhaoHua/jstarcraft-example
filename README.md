# JStarCraft Example

****

## 目录

* [介绍](#介绍)
* [特性](#特性)
* [安装](#安装)
* [使用](#使用)
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

JStarCraft Example Movie是一个基于[JStarCraft RNS引擎](https://github.com/HongZhaoHua/jstarcraft-rns),Spring Boot框架和[MovieLens 100K数据集](https://grouplens.org/datasets/movielens/100k/)搭建的电影演示项目,包括**个性化推荐**与**个性化搜索**两个部分.

系统会根据用户的行为记录,自动调整用户的推荐内容和搜索内容.

****

## 特性

#### 个性化推荐

* MostPopular
* ItemKNN
* UserKNN

#### 个性化搜索

* PageRank

****

## 安装

项目为了尽可能聚焦于个性化推荐和个性化搜索的演示,不包含任何多余组件的部署(例如MySQL/Redis/Spark/Elasticsearch).

JStarCraft Examlpe Movie只要求使用者具备以下环境:
* JDK 8或者以上
* Maven 3

****

## 使用

由于项目基于Spring Boot框架,使用者只需要直接执行com.jstarcraft.example.ExampleApplication,就可以运行项目.

使用个性化推荐,复制链接[http://127.0.0.1:8080?type=recommend](http://127.0.0.1:8080?type=recommend)到浏览器.

个性化推荐效果如图
![recommend](https://github.com/HongZhaoHua/jstarcraft-example/blob/master/recommend.png)

使用个性化搜索,复制链接[http://127.0.0.1:8080?type=search](http://127.0.0.1:8080?type=search)到浏览器.

个性化搜索效果如图
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
