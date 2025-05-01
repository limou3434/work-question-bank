package cn.com.edtechhub.workquestionbank.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.com.edtechhub.workquestionbank.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.com.edtechhub.workquestionbank.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 自定义权限加载接口实现类
 */
@Component // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@Slf4j
public class SaTokenStpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码值集合(暂时没有用到)
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) { // loginType 可以用来区分不同客户端
        Long userId = Long.valueOf(loginId.toString());
        List<String> list = new ArrayList<>();
        list.add("*"); // 有需要再实现
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合(权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) { // loginType 可以用来区分不同客户端
        // 从当前登录用户信息中获取角色
        User user = (User)StpUtil.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
        log.debug("检测一次当前用户的身份名称: {}", user.getUserRole());
        return Collections.singletonList(user.getUserRole());
    }

}
