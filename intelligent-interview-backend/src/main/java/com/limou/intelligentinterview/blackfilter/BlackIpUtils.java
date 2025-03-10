package com.limou.intelligentinterview.blackfilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * 黑名单过滤工具类
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Slf4j
public class BlackIpUtils {
    private static BitMapBloomFilter bloomFilter = new BitMapBloomFilter(100); // 创建一个布隆过滤器

    /**
     * 判断 ip 是否在黑名单里
     *
     * @param ip
     * @return
     */
    public static boolean isBlackIp(String ip) {
        return bloomFilter.contains(ip);
    }

    /**
     * 重建 ip 黑名单
     *
     * @param configInfo
     */
    public static void rebuildBlackIp(String configInfo) {
        if (StrUtil.isBlank(configInfo)) {
            configInfo = "{}";
        }

        // 解析 yaml 文件
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class); // 解析 YAML 格式的字符串, 并将其内容加载为一个 Map 对象, 便于程序处理 YAML 文件中的配置信息

        // 获取 IP 黑名单
        List<String> blackIpList = (List<String>) map.get("blackIpList");

        // 加锁防止并发
        synchronized (BlackIpUtils.class) { // 对 BlackIpUtils.class 类对象加锁, 确保在多线程环境下对该类相关的共享资源或代码块的访问是线程安全的
            if (CollUtil.isNotEmpty(blackIpList)) {
                BitMapBloomFilter bitMapBloomFilter = new BitMapBloomFilter(958506); // Is a magic number. 注意构造参数的设置
                for (String blackIp : blackIpList) {
                    bitMapBloomFilter.add(blackIp);
                }
                bloomFilter = bitMapBloomFilter;
            } else {
                bloomFilter = new BitMapBloomFilter(100);
            }
        }
    }
}