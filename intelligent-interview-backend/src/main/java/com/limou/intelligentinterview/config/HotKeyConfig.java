package com.limou.intelligentinterview.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hotkey 热 key 发现配置
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
// todo 取消注释开启 HotKey（须先配置 HotKey）
@Configuration
@ConfigurationProperties(prefix = "hotkey") // 表示配置文件(application.yml)中所有以 "hotkey." 开头的属性都会被映射到这个类的字段中
@Data
public class HotKeyConfig {

    /**
     * Etcd 服务器完整地址
     */
    private String etcdServer = "http://127.0.0.1:2379";

    /**
     * 应用名称
     */
    private String appName = "work-intelligent-interview";

    /**
     * 本地缓存最大数量
     */
    private int caffeineSize = 10000; // 使用本地缓存组件实现的本地缓存, 因此需要限制最大数量避免内存溢出

    /**
     * 批量推送 key 的间隔时间
     */
    private long pushPeriod = 1000L; // 先本地计算后再定时交给 worker 节点, 减少网络消耗

    /**
     * 初始化 hotkey
     */
    @Bean
    public void initHotkey() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName)
                .setCaffeineSize(caffeineSize)
                .setPushPeriod(pushPeriod)
                .setEtcdServer(etcdServer)
                .build();
        starter.startPipeline();
    }

}