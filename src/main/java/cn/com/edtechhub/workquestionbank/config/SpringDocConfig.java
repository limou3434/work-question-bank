package cn.com.edtechhub.workquestionbank.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "springdoc")
public class SpringDocConfig {

    /**
     * Knife4j Ui 在线文档地址
     */
    private String knife4jUi = "/doc.html";

    /**
     * Swagger Ui 在线文档地址
     */
//    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUi = "/swagger-ui.html";

    /**
     * 配置文档地址
     */
//    @Value("${springdoc.api-docs.path}")
    private String apiDocs = "/v3/api-docs";

}
