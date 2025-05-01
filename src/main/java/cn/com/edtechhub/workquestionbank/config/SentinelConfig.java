package cn.com.edtechhub.workquestionbank.config;

import cn.com.edtechhub.workquestionbank.common.BaseResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 配置
 * 可以选择在控制台制定规则, 也可以选择在这里初始规则后配合注解做对应的规则处理, 但是也会导致控制台失效
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class SentinelConfig {

    /**
     * UserStatus 资源流量控制异常处理方法(参数和接口保持一致)
     */
    private static BaseResponse<?> userStatusBlockHandler(BlockException ex) {

        // NOTE: 这里可以做一些策略(比如动态黑名单、组件降级操作...)

        return blockHandler(ex); // 最后一定要调用通用流量控制异常处理方法
    }

    /**
     * 初始化流量控制规则
     */
    public List<FlowRule> initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // NOTE: 定义规则

        return rules;
    }

    /**
     * 初始化熔断降级规则
     */
    public List<DegradeRule> initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // NOTE: 定义规则

        return rules;
    }

    /**
     * 初始化热点参数规则
     */
    public List<ParamFlowRule> initParamRules() {
        List<ParamFlowRule> rules = new ArrayList<>();

        // NOTE: 定义规则

        return rules;
    }

    /**
     * 初始化系统保护规则
     */
    public List<SystemRule> initSystemRules() {
        List<SystemRule> rules = new ArrayList<>();

        // NOTE: 定义规则

        return rules;
    }

    /**
     * 规则初始化方法(也可以选择在控制台中进行临时注入)
     */
    @PostConstruct // Spring 创建完 Bean 所有依赖注入完毕后, 调用被 @PostConstruct 标记的方法
    public void initRules() {
        // 加载所有规则, 但是也会导致控制台失效
//        FlowRuleManager.loadRules(initFlowRules());
//        DegradeRuleManager.loadRules(initDegradeRules());
//        ParamFlowRuleManager.loadRules(initParamRules());
//        SystemRuleManager.loadRules(initSystemRules());
    }

    /**
     * 通用流量控制异常处理方法
     */
    private static BaseResponse<?> blockHandler(BlockException ex) {
        // 流量控制异常
        if (ex instanceof FlowException) {
            return null;
//            return TheResult.error(CodeBindMessageEnums.FLOW_RULES, "请求频繁，请稍后重试");
        }
        // 熔断降级异常
        else if (ex instanceof DegradeException) {
            return null;
//            return TheResult.error(CodeBindMessageEnums.DEGRADE_RULES, "服务退化，请稍后重试");
        }
        // 热点参数异常
        else if (ex instanceof ParamFlowException) {
            return null;
//            return TheResult.error(CodeBindMessageEnums.PARAM_RULES, "资源过热，请稍后重试");
        }
        // 系统保护异常
        else {
            return null;
//            return TheResult.error(CodeBindMessageEnums.SYSTEM_RULES, "系统繁忙，请稍后重试");
        }
    }

}
