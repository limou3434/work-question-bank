package com.limou.intelligentinterview.blackfilter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * Nacos 监听器
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Slf4j
// @Component // NOTE: 手动取消动态过滤
public class NacosListener implements InitializingBean {

//    @NacosInjected
//    private ConfigService configService;

    @Value("${nacos.config.data-id}")
    private String dataId;

    @Value("${nacos.config.group}")
    private String group;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("nacos 监听器启动");
//
//        String config = configService.getConfigAndSignListener(dataId, group, 3000L, new Listener() {
//            final ThreadFactory threadFactory = new ThreadFactory() {
//                private final AtomicInteger poolNumber = new AtomicInteger(1);
//
//                @Override
//                public Thread newThread(@NotNull Runnable r) {
//                    Thread thread = new Thread(r);
//                    thread.setName("refresh-ThreadPool" + poolNumber.getAndIncrement());
//                    return thread;
//                }
//            };
//            final ExecutorService executorService = Executors.newFixedThreadPool(1, threadFactory);
//
//            // 通过线程池异步处理黑名单变化的逻辑
//            @Override
//            public Executor getExecutor() {
//                return executorService;
//            }
//
//            // 监听后续黑名单变化
//            @Override
//            public void receiveConfigInfo(String configInfo) {
//                log.info("监听到配置信息变化：{}", configInfo);
//                BlackIpUtils.rebuildBlackIp(configInfo);
//            }
//        });
//
//        // 初始化黑名单
//        BlackIpUtils.rebuildBlackIp(config);
    }
}
