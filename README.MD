## 基于caffeine和redis的多级缓存
![](https://img.shields.io/badge/license-MIT-000000.svg)

multi-cache 多级缓存基于caffeine和redis，目前支持以下功能

1. 支持本地缓存集群更新
2. 防缓存穿透
3. 支持同步load
... 


### 快速开始

1. import dependency

```xml
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>com.walm</groupId>
            <artifactId>multi-cache</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```
2. 初始化一个多级缓存
```java
    private Cache<UserPO> cache;
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @PostConstruct
    private void init() {
        cache = MultiCacheBuilder.newBuilder("cache-demo")
                .caffeineCache(100, 10000L, 5000L)
                .redisCache(redisTemplate, 20000L)
                .redisValueSerializer(new JsonValueSerializer<>(new TypeToken<UserPO>(){}))
                .build((key) -> {});

    }
```