package cn.com.edtechhub.workquestionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.com.edtechhub.workquestionbank.request.user.UserQueryRequest;
import cn.com.edtechhub.workquestionbank.model.entity.User;
import cn.com.edtechhub.workquestionbank.model.vo.LoginUserVO;
import cn.com.edtechhub.workquestionbank.model.vo.UserVO;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务声明
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登入
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登出
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户(允许未登录)
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 添加用户签到记录
     */
    boolean addUserSignIn(long userId);

    /**
     * 获取用户签到记录
     */
    List<Integer> getUserSignInRecord(long userId, Integer year);

}
