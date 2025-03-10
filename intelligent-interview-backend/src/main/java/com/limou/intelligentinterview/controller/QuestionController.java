package com.limou.intelligentinterview.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.cluster.TokenService;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limou.intelligentinterview.annotation.AuthCheck;
import com.limou.intelligentinterview.common.BaseResponse;
import com.limou.intelligentinterview.common.DeleteRequest;
import com.limou.intelligentinterview.common.ErrorCode;
import com.limou.intelligentinterview.common.ResultUtils;
import com.limou.intelligentinterview.constant.UserConstant;
import com.limou.intelligentinterview.exception.BusinessException;
import com.limou.intelligentinterview.exception.ThrowUtils;
import com.limou.intelligentinterview.manager.CounterManager;
import com.limou.intelligentinterview.model.dto.question.*;
import com.limou.intelligentinterview.model.entity.Question;
import com.limou.intelligentinterview.model.entity.User;
import com.limou.intelligentinterview.model.vo.QuestionVO;
import com.limou.intelligentinterview.service.QuestionService;
import com.limou.intelligentinterview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/xiaogithubooo">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private CounterManager counterManager;

    // region 增删改查

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（封装类）(crawler restriction)
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        /*
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
         */

        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 检测和处置爬虫（可以自行扩展为 - 登录后才能获取到答案）
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            crawlerDetect(loginUser.getId());
        }
        // 友情提示，对于敏感的内容，可以再打印一些日志，记录用户访问的内容
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 检测爬虫
     * @param loginUserId
     */
    private void crawlerDetect(long loginUserId) {
        // NOTE: 调用这个函数时, 已无需检测用户 id 是否存在了
        final int WARN_COUNT = 10; // TODO: 使用 nacos 来存储这个配置
        final int BAN_COUNT = 20;
        // TODO: Make Redis Const
        String key = String.format("user:access:%s", loginUserId); // TODO: 还可以检测用户最近一段时间访问的相关题目上下文, 如果访问的是同一个题目, 还不是顺序, 也有可能是正在复习的正常用户
        long count = counterManager.incrAndGetCounter(key, 1, TimeUnit.MINUTES, 180);

        if (count > BAN_COUNT) {
            // TODO: 向管理员发送短信通知

            // 下线
            StpUtil.kickout(loginUserId);

            // 封号
            User updateUser = new User();
            updateUser.setId(loginUserId);
            updateUser.setUserRole("ban"); // TODO: 由于存在封号机制, 因此最好每一个接口都填写权限校验而不是使用默认
            userService.updateById(updateUser);
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "您被系统检测为恶意爬虫, 现已封号, 若有误封请联系站长");
        }

        if (count == WARN_COUNT) { // NOTE: 要是大于每一次警告都会触发多份邮件
            // TODO: 向管理员发送邮件通知(使用 MailUtil)
            throw new BusinessException(110, "您被系统检测为疑似爬虫, 现发警告, 请珍惜您的个人帐号");
        }
    }

    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        return ResultUtils.success(questionPage);
    }
    // NOTE: 1.上面这些代码都会被提取拿来复用, 所以复制粘贴封装为 service

//    /**
//     * 分页获取题目列表（封装类）
//     *
//     * @param questionQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page/vo")
//    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(
//            @RequestBody QuestionQueryRequest questionQueryRequest,
//            HttpServletRequest request
//    ) {
//        // @RequestBody QuestionQueryRequest questionQueryRequest
//        // 数据来源: 通过 POST 请求体传入的数据, 通常由前端发送 JSON 格式的数据
//        // 使用 @RequestBody 注解后, Spring 会自动将 JSON 数据反序列化为 QuestionQueryRequest 对象, 这个对象包含了接口所需的查询参数
//        //
//        // HttpServletRequest request
//        // 数据来源：这是标准的 Servlet 请求对象，包含整个 HTTP 请求的上下文信息，由 Spring MVC 自动提供
//        // 它可以获取请求的元信息(如客户端 IP、请求头、Session 信息、Cookie 等)
//        // 在业务中, request 常用于获取用户认证信息、客户端 IP 地址、请求路径等，不直接用于查询条件
//
//        long current = questionQueryRequest.getCurrent(); // 获取请求中的 current(当前页数)
//        long size = questionQueryRequest.getPageSize(); // 获取请求中的 pageSize(页面条数)
//
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//
//        // 查询数据库
//        Page<Question> questionPage = questionService.page(
//            new Page<>(current, size),
//            questionService.getQueryWrapper(questionQueryRequest)
//        );
//
//        // 获取封装类
//        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
//    }

    /**
     * 分页获取题目列表（封装类）(degrade mechanism)(hparameter mechanism）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request
    ) {
        // 参数校验
        long current = questionQueryRequest.getCurrent(); // 获取请求中的 current(当前页数)
        long size = questionQueryRequest.getPageSize(); // 获取请求中的 pageSize(页面条数)
        String remoteAddr = request.getRemoteAddr();

        // TODO: 合法校验

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Entry entry = null;
        try {
            // 上报资源
            entry = SphU.entry("listQuestionVOByPage", EntryType.IN, 1, remoteAddr);

            // 查询数据库
            Page<Question> questionPage = questionService.page(
                    new Page<>(current, size),
                    questionService.getQueryWrapper(questionQueryRequest)
            );

            // 获取封装类
            return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));

        } catch (Throwable ex) {
            // 系统错误处理
            if (!BlockException.isBlockException(ex)) { // 如果只是普通异常而不是降级异常的处理方式
                Tracer.trace(ex); // NOTE: 需要手动上报
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
            }

            // 熔断机制处理
            if (ex instanceof DegradeException) {
                return handleFallback(questionQueryRequest, request, ex);
            }

            // 流量机制处理
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "请求次数过多, 请稍后再尝试");
        } finally {
            if (entry != null) {
                entry.exit(1, remoteAddr);
            }
        }
    }

    public BaseResponse<Page<QuestionVO>> handleFallback(
            @RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request,
            Throwable ex
    ) {
        return ResultUtils.success(null); // TODO: 可以返回本地数据或空数据
    }

    /**
     * 分页获取当前登录用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request
    ) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(
                new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest)
        );
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    /**
     * ES 高级查询接口
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<QuestionVO>> searchQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);

        // todo 取消注释开启 ES（须先配置 ES）
        // 查询 ES
        Page<Question> questionPage = questionService.searchFromEs(questionQueryRequest);

        // 查询数据库（作为没有 ES 的降级方案, 或者临时方案）
        // Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);

        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 批量删除接口
     *
     * @param questionBatchDeleteRequest
     * @return
     */
    @PostMapping("/delete/batch")
    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteQuestions(@RequestBody QuestionBatchDeleteRequest questionBatchDeleteRequest) {
        ThrowUtils.throwIf(questionBatchDeleteRequest == null, ErrorCode.PARAMS_ERROR);
        questionService.batchDeleteQuestions(questionBatchDeleteRequest.getQuestionIdList());
        return ResultUtils.success(true);
    }

}
