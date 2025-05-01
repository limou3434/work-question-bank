package cn.com.edtechhub.workquestionbank;

import cn.com.edtechhub.workquestionbank.config.ServerConfig;
import cn.com.edtechhub.workquestionbank.config.SpringDocConfig;
import cn.dev33.satoken.SaManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 启动类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */

@SpringBootApplication
@ServletComponentScan // 扫描 Servlet 组件以支持 IP 黑名单
@EnableScheduling // 开启定时任务
@Slf4j
public class WorkQuestionBankApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(WorkQuestionBankApplication.class, args);
        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        SpringDocConfig springdocConfig = context.getBean(SpringDocConfig.class);
        String baseUrl = "http://" + serverConfig.getAddress() + ":" + serverConfig.getPort() + serverConfig.getContextPath();
        log.info("Spring Boot running...");
        log.info("访问 {} 或 {} 即可得到在线文档, 访问 {} 即可得到文档配置", baseUrl + springdocConfig.getKnife4jUi(), baseUrl + springdocConfig.getSwaggerUi(), baseUrl + springdocConfig.getApiDocs());
        log.debug("读取 Sa-token 配置查验是否正确: {}", SaManager.getConfig());
        log.debug("读取 Sa-token 切面类查验是否被替换为自己的: {}", SaManager.getStpInterface());
    }

}
