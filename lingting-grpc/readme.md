```mermaid
graph TB
    A("GrpcTraceIdInterceptor") --> B("GrpcThreadExecutorInterceptor")
    B --> C("GrpcServerCompressionInterceptor")
    C --> D("SecurityGrpcResourceServerInterceptor")

```
