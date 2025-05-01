package cn.com.edtechhub.workquestionbank.config;

import cn.com.edtechhub.workquestionbank.utils.BlackIpUtils;
import cn.com.edtechhub.workquestionbank.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 黑名单配置
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@WebFilter(urlPatterns = "/*", filterName = "BlackIpConfig")
@Slf4j
public class BlackIpConfig implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String ipAddress = IpUtils.getIpAddress((HttpServletRequest) servletRequest);

        log.debug("拦截器 BlackIpConfig 检测到客户端 IP 地址: {}", ipAddress);

        if (BlackIpUtils.isBlackIp(ipAddress)) {
            log.debug("拦截器 BlackIpConfig 检测到客户端 IP 地址 {} 在黑名单中, 禁止访问", ipAddress);
            servletResponse.setContentType("text/json;charset=UTF-8");

            servletResponse.getWriter().write("{\"code\":\"-1\",\"message\":\"黑名单 IP, 禁止访问\",\"data\":null}");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
