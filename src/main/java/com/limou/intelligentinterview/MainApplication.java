package com.limou.intelligentinterview;

import cn.dev33.satoken.SaManager;
import com.limou.intelligentinterview.config.ServerConfig;
import com.limou.intelligentinterview.config.SpringDocConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */

// @SpringBootApplication (exclude = {RedisAutoConfiguration.class}) // TODO: 如需开启 Redis，须移除 exclude 中的内容
@SpringBootApplication
@MapperScan("com.limou.intelligentinterview.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
// @ServletComponentScan // NOTE: 手动取消动态过滤
@Slf4j
public class MainApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(MainApplication.class, args);
        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        SpringDocConfig springdocConfig = context.getBean(SpringDocConfig.class);
        String baseUrl = "http://" + serverConfig.getAddress() + ":" + serverConfig.getPort() + serverConfig.getContextPath();
        log.info("Spring Boot running...");
        log.info("访问 {} 或 {} 即可得到在线文档, 访问 {} 即可得到文档配置", baseUrl + springdocConfig.getKnife4jUi(), baseUrl + springdocConfig.getSwaggerUi(), baseUrl + springdocConfig.getApiDocs());
        log.debug("读取 Sa-token 配置查验是否正确: {}", String.valueOf(SaManager.getConfig()));
        log.debug("读取 Sa-token 切面类查验是否被替换为自己的: {}", String.valueOf(SaManager.getStpInterface()));
    }

}
