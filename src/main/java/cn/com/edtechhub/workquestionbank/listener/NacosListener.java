package cn.com.edtechhub.workquestionbank.listener;


import cn.com.edtechhub.workquestionbank.utils.BlackIpUtils;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Nacos 监听器
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Slf4j
@Component
public class NacosListener implements InitializingBean {

    /**
     * 配置文件标识
     */
    @Value("${nacos.config.data-id}")
    private String dataId;

    /**
     * 配置文件分组
     */
    @Value("${nacos.config.group}")
    private String group;

    /**
     * 注入 Nacos 配置服务
     */
    @NacosInjected
    private ConfigService configService;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("启动 nacos 监听器");
        log.debug("读取配置为 dataId: {} 和 group: {}", dataId, group);

        // 获取配置文件
        String config = configService.getConfigAndSignListener(
                dataId, // 配置文件标识
                group,  // 配置文件分组
                3000L,  // 监听间隔时间
                new Listener() { // 监听器
                    final ThreadFactory threadFactory = new ThreadFactory() {
                        private final AtomicInteger poolNumber = new AtomicInteger(1);

                        @Override
                        public Thread newThread(@NotNull Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("refresh-ThreadPool" + poolNumber.getAndIncrement());
                            return thread;
                        }
                    };

                    // 创建只有一个线程的线程池, 用来异步执行 Nacos 配置监听收到的处理逻辑
                    final ExecutorService executorService = Executors.newFixedThreadPool(1, threadFactory);

                    // 通过线程池异步处理黑名单变化的逻辑
                    @Override
                    public Executor getExecutor() {
                        return executorService;
                    }

                    // 监听后续黑名单变化
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        log.debug("监听到配置信息发生变化, 准备转移配置重构布隆过滤器");
                        BlackIpUtils.rebuildBlackIp(configInfo);
                    }
                });

        // 初始化黑名单
        BlackIpUtils.rebuildBlackIp(config);
    }

}
