package cn.com.edtechhub.workquestionbank.controller;

import cn.com.edtechhub.workquestionbank.common.BaseResponse;
import cn.com.edtechhub.workquestionbank.common.DeleteRequest;
import cn.com.edtechhub.workquestionbank.common.ErrorCode;
import cn.com.edtechhub.workquestionbank.common.ResultUtils;
import cn.com.edtechhub.workquestionbank.exception.BusinessException;
import cn.com.edtechhub.workquestionbank.exception.ThrowUtils;
import cn.com.edtechhub.workquestionbank.manager.CounterManager;
import cn.com.edtechhub.workquestionbank.model.entity.Question;
import cn.com.edtechhub.workquestionbank.model.entity.User;
import cn.com.edtechhub.workquestionbank.model.vo.QuestionVO;
import cn.com.edtechhub.workquestionbank.request.question.*;
import cn.com.edtechhub.workquestionbank.service.QuestionService;
import cn.com.edtechhub.workquestionbank.service.UserService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    /**
     * 注入题目服务依赖
     */
    @Resource
    private QuestionService questionService;

    /**
     * 注入用户服务依赖
     */
    @Resource
    private UserService userService;

    /**
     * 注入计数器依赖
     */
    @Resource
    private CounterManager counterManager;

    /**
     * 创建题目
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/add")
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
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/update")
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
     * 查询题目
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/list/page")
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        return ResultUtils.success(questionPage);
    }

    /**
     * 批量删除题目
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/delete/batch")
    public BaseResponse<Boolean> batchDeleteQuestions(@RequestBody QuestionBatchDeleteRequest questionBatchDeleteRequest) {
        ThrowUtils.throwIf(questionBatchDeleteRequest == null, ErrorCode.PARAMS_ERROR);
        questionService.batchDeleteQuestions(questionBatchDeleteRequest.getQuestionIdList());
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目
     */
    @SaCheckLogin
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 检测和处置爬虫（可以自行扩展为 - 登录后才能获取到答案）
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            crawlerDetect(loginUser.getId());
//        }
        // 友情提示，对于敏感的内容，可以再打印一些日志，记录用户访问的内容
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取题目列表
     */
    @SaIgnore
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        // 参数校验
        long current = questionQueryRequest.getCurrent(); // 获取请求中的 current(当前页数)
        long size = questionQueryRequest.getPageSize(); // 获取请求中的 pageSize(页面条数)
//        String remoteAddr = request.getRemoteAddr();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

//        Entry entry = null;
//        try {
//            // 上报资源
//            entry = SphU.entry("listQuestionVOByPage", EntryType.IN, 1, remoteAddr);

        // 查询数据库
        Page<Question> questionPage = questionService.page(
                new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest)
        );

        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));

//        } catch (Throwable ex) {
//            // 系统错误处理
//            if (!BlockException.isBlockException(ex)) { // 如果只是普通异常而不是降级异常的处理方式
//                Tracer.trace(ex); // NOTE: 需要手动上报
//                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
//            }
//
//            // 熔断机制处理
//            if (ex instanceof DegradeException) {
//                return handleFallback(questionQueryRequest, request, ex);
//            }
//
//            // 流量机制处理
//            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "请求次数过多, 请稍后再尝试");
//        } finally {
//            if (entry != null) {
//                entry.exit(1, remoteAddr);
//            }
//        }
    }

    /**
     * 分页获取当前登录用户创建的题目列表
     */
    @SaCheckLogin
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
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
     */
    @SaCheckLogin
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest) {
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
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * ES 高级查询接口
     */
    @SaCheckLogin
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<QuestionVO>> searchQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);

        Page<Question> questionPage = questionService.searchFromEs(questionQueryRequest); // Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 检测爬虫
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

}
