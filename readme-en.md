#### Tool module

`core` `jackson` `http` etc.

> Used to access any Java/Kotlin project as a tool module

#### Dependency management module

`lingting-depencies`

> Used for unified dependency management. Upstream uses `spring-boot-dependencies` as the basic dependency source,
> trustworthy

#### Third-party API access

`ali` `aws` `dingtalk` etc.

> Handwritten third-party API implementation. Avoid the introduction of third-party SDKs causing dependency conflicts
>
> Provide various ways of access points, customize your own partial implementation logic

#### Authentication module

`security` `security-grpc`

- Because the `security-web` depends on `spring`, it moved to the `lingting-spring` project

> Simple authentication module, fast access authentication.
>
> Built-in some basic user attributes, supports rapid expansion of your own user attributes
>
> Provide custom authentication access to define your own authentication logic
>
> Provides custom token processing, allowing quick access to third-party login
