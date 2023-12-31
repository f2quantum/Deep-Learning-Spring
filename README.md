# 深度学习计算&存储服务
Deep-Learning-JavaEE-SpringBoot
## -1.前言
2023年的实习形势，简历上没有一个涉猎广泛的玩具项目简直是不行，鉴于当前深度学习~~十分甚至是九分~~的红火，鄙人参考了一些GitHub上SpringBoot+中间件的一些web项目和基于Java中[Deep Java Library: DJL](https://djl.ai/)库，自己缝合了一个提供深度学习计算与存储服务的J2EE玩具。

初步想法是2022年11月有的，~~如果现在来看的话，搞AIGC更能蹭热度~~ 。

以后可能会加点微服务！~~（饼）~~

## 0.描述
一个基于Springboot3 的实习/秋招J2EE玩具项目，目前的功能是提供一个简单的调用深度学习框架进行模型管理和图像识别任务http接口，你可以通过swagger api的形式对其进行简单的调用。

~~你可以在面试的时候这样说（忽悠）~~：

该项目是一个负责收集来自工程中传感器网络反馈的信息并对深度学习模型进行训练和预测的系统。

主要功能是对来自边缘设备的线性及多模态反馈数据的预测和建模，在项目中负责服务器架构设计以及后端服务开发。


使用Docker Compose对Docker容器进行编排管理

特点：
- 使用MySQL对模型及数据进行持久化保存
- 针对大量相同的分析预测和模型请求，采用 Redis 作为中间件缓存，提高了并发访问的负载能力。
- 采用RabbitMQ异步解决请求堆积问题，解耦、异步、削峰。
- 底层使用djl库调用工业界常用的深度学习框架进行训练和预测分析
## 1. 技术栈：
- JDK：
  - openjdk 19
- J2EE容器： 
  - Springboot 3.0.2 [Spring Initializr - Spring Boot](https://start.spring.io/)
- 持久层：
  - MySQL: mariadb:latest [mariadb](https://hub.docker.com/_/mariadb)
- 中间件：
  - 缓存：
    - Redis: [redis:latest](https://hub.docker.com/layers/library/redis/latest/images/sha256-178215249742b63308db1a5373a7c627714c582362f3dcd24b2eec849dc16e67?context=explore)
  - 消息队列：
    - Rabbitmq: [rabbitmq:latest](https://hub.docker.com/_/rabbitmq)


## 2. 模块构成 ~~（棒读）~~
### 1. 模型分发与模型管理
   - 模型在系统中使用Mysql中的blob进行二进制文件的保存
   - 模型分发使用了Redis作为缓存，缓存了常见的模型二进制文件
   - 负责管理模型的加载和持久化

### 2. 模型训练
   - 接受来自设备的数据与参数，采用djl 库 进行训练，并在训练结束后通过mysql持久化
   - 训练的过程使用了状态机模式进行设计 （初始-> 加载数据 -> 就绪 -> 开始训练 ->训练完成）、
   - 加载数据后模型配置及数据信息提交到消息队列中，等待训练服务进行消费
   - 训练任务使用Mysql 进行持久化 训练生成的模型通过模型管理服务进行保存

### 3. 预测分析
   - 通过模型管理模块加载djl模型 进行预测分析
   - 图片预测 ：输入一张图片 输出 预测信息（例如 目标检测）
   - 线性预测： 输入一个矩阵/行 输出预测信息
   - 使用了RabbitMQ 使用消息队列进行抗压，异步解决请求堆积问题
     - Sender 将 输入 输送进消息队列 并返回一个sessionID
     - Receiver 从消息队列中获取预测请求，并交给djl库中的模型进行处理，并将输出后的结果保存到redis中 ，用户可以用sessionID作为key 进行查询
   - 压力测试：使用locust框架对目标检测的web 接口执行压力测试可以达到100 qps
   - 接口限速 ： 定义了一个 RateLimit 注解，基于 Guava RateLimiter 使用AOP 框架对请求进行进行拦截 + ConcurrentHashMap<String, RateLimiter>保存了不同设备的device id 