#### 工具模块

`core` `jackson` `http` 等

> 用于接入任意 Java/Kotlin 项目作为工具模块使用

#### 依赖管理模块

`lingting-depencies`

> 用于统一依赖管理. 上游使用 `spring-boot-dependencies` 作为基础依赖来源, 值得信任

#### 第三方API接入

`ali` `aws` `dingtalk` 等

> 手写第三方API实现. 避免引入第三方SDK导致依赖冲突
> 提供各种方式的接入点, 自定义自己的部分实现逻辑

#### 鉴权模块

`security` `security-grpc`

- 由于 `security-web` 依赖 `spring` 所以迁移到了 `lingting-spring` 项目

> 简易的鉴权模块, 快速接入鉴权.
> 内置部分用户基础属性, 支持快速扩展自有的用户属性
> 提供自定义鉴权接入, 可定义自己的鉴权逻辑
> 提供自定义令牌处理, 可以快速接入第三方登录
