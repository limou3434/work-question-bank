package com.limou.intelligentinterview.aop;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求响应日志 AOP
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 **/
@Aspect // 表示该类是一个切面, 包含了与某个横切关注点相关的逻辑
@Component // 标识这是一个 Spring Bean, 这样才会被 Spring 进行容器管理和实例化, 用户无需手动进行创建
@Slf4j // 该注解来自 Lombok, 用于自动生成一个名为 log 的日志记录器(变量), 这样就可以通过 log 变量记录日志信息
/*
    Lombok 是一个 Java 库, 旨在简化 Java 开发
    尤其是在编写模型类(POJO)时的样板代码
    通过使用 Lombok 可以减少常见的样板代码

    @Getter 和 @Setter：自动生成类属性的 getter 和 setter 方法
    @ToString：自动生成 toString 方法
    @EqualsAndHashCode：自动生成 equals 和 hashCode 方法
    @NoArgsConstructor 和 @AllArgsConstructor：自动生成无参和全参构造函数
    @Builder：提供建造者模式的支持, 使得对象的创建更加灵活
    @Value：用于创建不可变对象(immutable object)
    @Equals 和 @hashCode: ...

    其中 @Slf4j 是 Lombok 提供的一个注解, 用于自动生成 SLF4J(Simple Logging Facade for Java)日志记录器
    使用该注解就可以在类中直接使用 log 对象记录日志, 而无需手动声明和初始化 Logger 实例
*/
public class LogInterceptor {

    /**
     * 执行拦截
     */
    @Around("execution(* com.limou.intelligentinterview.controller.*.*(..))") // 根据之前所学, 就可以知道下面这个调用在方法调用前后都会执行消息, 切入点是 controller 下的所有类和方法
    // 因此可以看出 AOP 切面可以使用上述的字符匹配, 也可以使用注解匹配(之前鉴权就是使用注解匹配)
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // StopWatch 是 Spring 框架提供的一个工具类, 用于简化时间测量的过程
        StopWatch stopWatch = new StopWatch();

        // 开始计时
        stopWatch.start();

        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes(); // 获取当前线程的请求上下文, 这是一个线程局部存储, 用于在不同线程间保存请求信息, 允许每个线程独立存储数据, 这样不同线程之间的数据不会相互干扰
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 生成请求 uuid
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();

        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";

        // 输出请求日志
        log.info("request start，id: {}, path: {}, client ip: {}, params: {}", requestId, url, httpServletRequest.getRemoteHost(), reqParam);

        // 执行原方法
        Object result = point.proceed();

        // 停止计时
        stopWatch.stop();

        // 输出总时间
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);

        return result;
    }
}
