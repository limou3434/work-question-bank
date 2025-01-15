package com.limou.intelligentinterview.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */

// java 中的注解通过 @interface 关键字进行定义, 并且可以指定其元注解
@Target(ElementType.METHOD) // 这个元注解控制作用范围
@Retention(RetentionPolicy.RUNTIME) // 这个元注解控制生命周期
/*
    @Target 的值还可以是:
    ElementType.TYPE: 类、接口、枚举
    ElementType.METHOD: 方法
    ElementType.FIELD: 字段
    ElementType.PARAMETER: 参数

    @Retention 的值还可以是:
    RetentionPolicy.SOURCE：只在源码中存在, 编译后会被丢弃, 这种即不会出现在于编译时, 也不会存在于运行时的注解通常用于编译器进行静态代码校验(或者屏蔽某些拼写错误, 警告某些用法已经过时), 也可以当作注释使用, 方便某些文档工具生成文档
    RetentionPolicy.CLASS：在字节码中存在, 但在运行时不可见(默认), 这种注解通常用于实现编译器插件和工具链, 这些工具(代码生成器, 字节码增强器)会在编译或类加载阶段处理这些注解, 而运行时不可见就是指减少在运行时的开销, 只在编译时期进行处理
    RetentionPolicy.RUNTIME：在运行时可见, 可以通过反射访问, 这种类型的注释无论是编译时还是运行时都可以被反射机制获取到, 通常用来做运行时动态处理(例如面向切面编程的 AOP, 依赖注入等)

    自定义注解的主要目的是为了 AOP 面向切面编程, 将和代码逻辑无关的日志、权限管理、事务管理...和业务逻辑解耦开
 */
public @interface AuthCheck {
    /**
     * 这里实现了 AuthCheck 注解, AOP 切面处理时就会
     *
     * @return
     */
    String mustRole() default ""; // 这是注解中的一种语法, 用来定义注解的属性, 这里就定义了一个 mustRole 属性, 类型是 String, 这里本质上是定义了一个方法, 而方法必须包含参数列表，因此就有一个 (), 但是这个参数列表必须为空, 不过却允许有一个默认 default 参数, 这里的默认参数是空字符

    /*
        注解的本质是提供元数据, 因此注解内部不存在包含逻辑的方法，只有很多类型的属性, 通常有如下几种属性
        String value(); // 必填属性，使用时需要指定值
        int count() default 1; // 有默认值的属性，可以选择性地指定
        boolean enabled() default true; // 布尔类型的属性
        Class<?> targetClass(); // 类类型的属性(可以是任意的类类型)
        String[] roles() default {}; // 数组类型的属性
    */
}

// 但是注解只是定义了一个标识, 没有做出实际的事情, 还需要依赖 AOP 面向切面编程完成校验身份的具体逻辑(这也是自定义注解的主要目的)
