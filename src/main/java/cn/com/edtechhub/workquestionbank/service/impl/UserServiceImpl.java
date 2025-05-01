package cn.com.edtechhub.workquestionbank.service.impl;

import cn.com.edtechhub.workquestionbank.common.ErrorCode;
import cn.com.edtechhub.workquestionbank.config.MyBatisPlusConfig;
import cn.com.edtechhub.workquestionbank.constant.RedisConstant;
import cn.com.edtechhub.workquestionbank.constant.UserConstant;
import cn.com.edtechhub.workquestionbank.exception.BusinessException;
import cn.com.edtechhub.workquestionbank.mapper.UserMapper;
import cn.com.edtechhub.workquestionbank.model.entity.User;
import cn.com.edtechhub.workquestionbank.model.vo.LoginUserVO;
import cn.com.edtechhub.workquestionbank.model.vo.UserVO;
import cn.com.edtechhub.workquestionbank.request.user.UserQueryRequest;
import cn.com.edtechhub.workquestionbank.service.UserService;
import cn.com.edtechhub.workquestionbank.utils.DeviceUtils;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 注入 redisson 客户端依赖
     */
    @Resource
    private RedissonClient redissonClient;

    /**
     * 注入 MyBatis Plus 配置依赖
     */
    @Resource
    private MyBatisPlusConfig mybatisPlusConfig;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 5 || checkPassword.length() < 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }

            // 加密密码
            String encryptPassword = DigestUtils.md5DigestAsHex((mybatisPlusConfig.getSalt() + userPassword).getBytes());

            // 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((mybatisPlusConfig.getSalt() + userPassword).getBytes());

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 登陆
        StpUtil.login(user.getId(), DeviceUtils.getRequestDevice(request));

        // 使用会话来存储用户信息
        StpUtil.getSessionByLoginId(user.getId()).set(UserConstant.USER_LOGIN_STATE, user);

        return this.getLoginUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        StpUtil.logout(); // 默认所有设备都退出登陆
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object loginUserId = StpUtil.getLoginIdDefaultNull();
        if (loginUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        User currentUser = this.getById((String) loginUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;

        /* 如果用户信息几乎很少修改，可以不查数据库，直接从 Sa-Token 的 Session 中获取之前保存的用户登录态
        // 先判断是否已登录
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (Objects.isNull(loginId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (User) StpUtil.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
         */
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        Object loginUserId = StpUtil.getLoginIdDefaultNull();
        if (loginUserId == null) {
            return null;
        }

        return this.getById((String) loginUserId);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);
        return queryWrapper;
    }

    @Override
    public boolean addUserSignIn(long userId) {
        LocalDate now = LocalDate.now();
        String key = RedisConstant.getUserSignInRedisKey(now.getYear(), userId);
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        int offset = now.getDayOfYear();
        if (!signInBitSet.get(offset)) { // 尚未签到则签到
            signInBitSet.set(offset, true);
        }
        return true;
    }

    @Override
    public List<Integer> getUserSignInRecord(long userId, Integer year) {
        if (year == null) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
        }
        String key = RedisConstant.getUserSignInRedisKey(year, userId);
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        BitSet bitset = signInBitSet.asBitSet(); // 将获取到的值缓存到 java 中, 避免大量的网络消耗
        List<Integer> dayList = new ArrayList<>();
        int index = bitset.nextSetBit(0); // 从索引 0 开始查找下一个被设置为 1 的位置, 使用 nextSetBit() 可以将循环的颗粒度降低
        while (index >= 0) {
            dayList.add(index);
            index = bitset.nextSetBit(index + 1); // 从索引 0 开始查找下一个被设置为 1 的位置
        }
        return dayList;
    }
}
