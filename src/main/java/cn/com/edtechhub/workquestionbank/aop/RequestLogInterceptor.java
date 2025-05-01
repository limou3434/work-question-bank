package cn.com.edtechhub.workquestionbank.aop;

import cn.com.edtechhub.workquestionbank.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求日志拦截切面
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {

    /**
     * 每次网络接口被调用都会执行这个方法
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug("拦截到请求: {}", "来自 " + IpUtils.getIpAddress(request) + " - " + request.getMethod() + " " + request.getRequestURI());

        return true; // 返回 false 会终止请求, 可以利用这一点进行 IP 屏蔽

    }

}
