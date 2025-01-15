package com.limou.intelligentinterview.aop;

import com.limou.intelligentinterview.annotation.AuthCheck;
import com.limou.intelligentinterview.common.ErrorCode;
import com.limou.intelligentinterview.exception.BusinessException;
import com.limou.intelligentinterview.model.entity.User;
import com.limou.intelligentinterview.model.enums.UserRoleEnum;
import com.limou.intelligentinterview.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Aspect // 声明这个类是一个切面, 用于拦截方法的执行并且插入自定义逻辑
@Component // 将这个类注册 Spring 的组件, 这会自动管理这个组件的生命周期(放入某个容器中进行管理), 无需用户实例化
// 切面类必须作为 Spring 容器中的 Bean, 才能被自动扫描和应用到程序中.
// 因此使用 @Component 可以让 Spring 识别到这个类, 并使其具备拦截功能(实际上是扫描所有带有 @Service 和 @Component 标识的).
// Spring 在启动时会自动扫描 @Component 注解的类，并将其注册到 IoC 容器中
// IoC 也就是 "控制反转", 强调将对象的创建和依赖关系的管理从应用程序代码中剥离出来, 交给外部容器来处理
public class AuthInterceptor {
    @Resource // 注入 UserService 服务类对象到 AuthInterceptor 类中
    private UserService userService; // UserService 这个类类型可以后面再来解释具体实现
    // 注入后就可以使用一些公共方法, 后面这个类中的代码就有用到

    /**
     * 执行拦截
     *
     * @param joinPoint 表示被拦截方法的信息(可以用来获取调用方法的方法名称, 参数值, 目标对象)
     * @param authCheck 表示当前被拦截方法的注解实例(内部包含使用注解时传递的参数, 在这个注解里就是我们之前提到的 mustRole 注解属性)
     * @return
     */
    @Around("@annotation(authCheck)") // @Around 确定切入点, @annotation 表示对所有 @AuthCheck 进行拦截
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable { // 有可能抛出根异常, 这样抛出所有类型的异常
        // 获取注解传递的角色
        String mustRole = authCheck.mustRole(); // 获取必须的角色, 该角色名称是在注解中定义的注解属性, 用户在使用 @AuthCheck 注解时需要传递角色名词, 这样这里才可以获得角色名称
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole); // 将获取的 mustRole 转换为 UserRoleEnum 枚举类型

        // 获取当前登录的用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes(); // RequestContextHolder 是 Spring 提供的一个工具类, 用于获取当前线程的请求上下文
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest(); // 这部分代码可以不用细纠
        User loginUser = userService.getLoginUser(request); // 通过使用 userService 的公共方法获取当前登陆用户
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole()); // 将 loginUser.getUserRole() 装换为 UserRoleEnum 枚举类型

        // 检查用户权限
        // 如果转换后 mustRoleEnum 结果为空则直接通过
        if (mustRoleEnum == null) {
            return joinPoint.proceed(); // 才能执行目标方法
        }
        // 如果转化后 userRoleEnum 结果为空则直接拒绝
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 如果被封号则直接拒绝
        if (UserRoleEnum.BAN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 如果注解说明 mustRoleEnum 必须有管理员权限, 而 userRoleEnum 不是管理员账号则直接拒绝
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum)) {
            // 用户没有管理员权限，拒绝
            if (!UserRoleEnum.ADMIN.equals(userRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 只要不包含上述情况就直接通过权限校验
        return joinPoint.proceed(); // 才能执行目标方法
    }
}

/*
    总结一下的话, 注解的使用流程如下:
    1.定义注解接口
    使用 "public @interface 注解名" 自定义注解
    然后添加一些元注解 @Documented(生成文档), @Retention(生命周期), @Target(作用类型)

    2.定义拦截接口
    @Component 将这个类注册 Spring 的组件, 这会自动管理这个组件的生命周期(放入某个容器中进行管理), 无需用户实例化
    @Aspect() 标记一个切面, 用来定义 "切点+通知"
    @Before() 根据参数匹配指定切点, 在指定方法调用之前完成消息通知, 可以获取 JoinPoint 对象
    @After() 根据参数匹配指定切点, 在指定方法调用之后完成消息通知(无论是否抛出异常) 可以获取 JoinPoint 对象
    @Around() 根据参数匹配指定切点, 在指定方法调用前后都完成不同的消息通知, 可以获取 ProceedingJoinPoint 对象控制指定方法的执行(上面代码就是截取了 joinPoint 的前后, 只有符合权限要求才能执行, 否则抛出异常), 不过这种情况下如果 doInterceptor() 就必须显示返回 joinPoint.proceed(), 若返回 null 则会导致指定的切入方法无法被调用
    @AfterReturning() 根据参数匹配指定切点, 在指定方法调用成功后执行, 可以获取 JoinPoint 对象和 Object 返回值对象
    @AfterThrowing() 根据参数匹配指定切点, 在指定方法调用抛出异常后执行, 可以获取 JoinPoint 对象和 Throwable 异常对象
    @Pointcut() 定义可复用的切点, 可以供给不同的通知复用, 这样其他的消息就不用重复书写同一个指定切点的匹配模式

    3.代码使用注解
    在符合注解要求的代码对象前加上注解就行

    注意: 避免在主代码中直接使用 @Autowired 来获取这个注解实例(这么做会破坏 AOP 的精髓)
*/
