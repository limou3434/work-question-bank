package cn.com.edtechhub.workquestionbank.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 黑名单工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Slf4j
public class BlackIpUtils {

    /**
     * 预期插入数量
     */
    private static final int EXPECTED_INSERTIONS = 10000;

    /**
     * 误判率(0.1%)
     */
    private static final double FALSE_POSITIVE_PROBABILITY = 0.001;

    /**
     * 布隆过滤器实例
     */
    private static final AtomicReference<BloomFilter<String>> bloomFilterRef = new AtomicReference<>(createNewBloomFilter()); // 使用 Guava BloomFilter 实现, 使用原子性引用容器能让你在多线程环境下, 以无锁的方式安全地读取和替换一个对象引用

    /**
     * 创建新的布隆过滤器实例
     */
    private static BloomFilter<String> createNewBloomFilter() {
        return BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()),
                EXPECTED_INSERTIONS,
                FALSE_POSITIVE_PROBABILITY
        );
    }

    /**
     * 重新构建黑名单布隆过滤器
     */
    public static void rebuildBlackIp(String configInfo) {
        log.debug("重新构建黑名单布隆过滤器时传入的配置信息: {}", configInfo);
        if (configInfo == null || configInfo.trim().isEmpty()) {
            configInfo = "blackIpList:\n" +
                    "    - \"1.1.1.1\"\n" +
                    "    - \"2.2.2.2\"\n" +
                    "    - \"3.3.3.3\""; // 默认列表, 一般是不会为空的, 除非 Nacos 挂掉, 这里做了一点降级策略
        }

        // 解析 yaml 配置
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(configInfo);

        // 安全获取黑名单列表
        List<String> blackIpList = Optional.ofNullable(map)
                .map(m -> (List<String>) m.get("blackIpList")) // 这里是从远端读取的配置文件名称
                .orElse(Collections.emptyList());

        // 创建新的布隆过滤器
        BloomFilter<String> newBloomFilter = createNewBloomFilter();

        // 添加 IP 到新过滤器
        if (blackIpList != null && !blackIpList.isEmpty()) {
            for (String ip : blackIpList) {
                if (ip != null && !ip.trim().isEmpty()) {
                    newBloomFilter.put(ip.trim());
                }
            }
        }

        // 原子性更新引用
        bloomFilterRef.set(newBloomFilter);
        log.debug("布隆过滤器已更新, 当前黑名单数量: {}", blackIpList.size());
    }

    /**
     * 判断 IP 是否在黑名单内
     */
    public static boolean isBlackIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        boolean mightContain = bloomFilterRef.get().mightContain(ip.trim());
        log.debug("检测客户端 IP 地址 {} 此时是否在黑名单内: {}", ip, mightContain);
        return mightContain;
    }
}
