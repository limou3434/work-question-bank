package cn.com.edtechhub.workquestionbank.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "spring.redis") // 这里会读取 application.yml 下的 spring 下的 redis
@Data
public class RedissonConfig {

    private String host;

    private Integer port;

    private Integer database;

    private String password;

    // 初始化 Redisson 客户端
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setPassword(password == null || password.isEmpty() ? null : password);
        return Redisson.create(config);
    }

}

// TODO: 需要做键值过期, 这个过期至少是一年
// TODO: 可以修改为具体时间和刷题数量
