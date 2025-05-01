package cn.com.edtechhub.workquestionbank.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import cn.com.edtechhub.workquestionbank.common.BaseResponse;
import cn.com.edtechhub.workquestionbank.common.DeleteRequest;
import cn.com.edtechhub.workquestionbank.common.ErrorCode;
import cn.com.edtechhub.workquestionbank.common.ResultUtils;
import cn.com.edtechhub.workquestionbank.exception.BusinessException;
import cn.com.edtechhub.workquestionbank.exception.ThrowUtils;
import cn.com.edtechhub.workquestionbank.model.entity.Question;
import cn.com.edtechhub.workquestionbank.model.entity.QuestionBank;
import cn.com.edtechhub.workquestionbank.model.entity.User;
import cn.com.edtechhub.workquestionbank.model.vo.QuestionBankVO;
import cn.com.edtechhub.workquestionbank.model.vo.QuestionVO;
import cn.com.edtechhub.workquestionbank.request.question.QuestionQueryRequest;
import cn.com.edtechhub.workquestionbank.request.questionBank.QuestionBankAddRequest;
import cn.com.edtechhub.workquestionbank.request.questionBank.QuestionBankEditRequest;
import cn.com.edtechhub.workquestionbank.request.questionBank.QuestionBankQueryRequest;
import cn.com.edtechhub.workquestionbank.request.questionBank.QuestionBankUpdateRequest;
import cn.com.edtechhub.workquestionbank.service.QuestionBankService;
import cn.com.edtechhub.workquestionbank.service.QuestionService;
import cn.com.edtechhub.workquestionbank.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库接口
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@RestController
@RequestMapping("/questionBank")
@Slf4j
public class QuestionBankController {

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建题库
     *
     * @param questionBankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
//    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankAddRequest, questionBank);
        // 数据校验
        questionBankService.validQuestionBank(questionBank, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        questionBank.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionBankService.save(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankId = questionBank.getId();
        return ResultUtils.success(newQuestionBankId);
    }

    /**
     * 删除题库
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
//    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = questionBankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库（仅管理员可用）
     *
     * @param questionBankUpdateRequest
     * @return
     */
    @PostMapping("/update")
//    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
        if (questionBankUpdateRequest == null || questionBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankUpdateRequest, questionBank);
        // 数据校验
        questionBankService.validQuestionBank(questionBank, false);
        // 判断是否存在
        long id = questionBankUpdateRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库（封装类）(set first-level-cache)
     *
     * @param questionBankQueryRequest
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankVO> getQuestionBankVOById(QuestionBankQueryRequest questionBankQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);

        Long id = questionBankQueryRequest.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // search cache
//        String key = "bank_detail_" + id;
//        if (JdHotKeyStore.isHotKey(key)) {
//            Object cacheQuestionBankVO = JdHotKeyStore.get(key);
//            if (cacheQuestionBankVO != null) {
//                return ResultUtils.success((QuestionBankVO) cacheQuestionBankVO);
//            }
//        }

        // 查询数据库
        QuestionBank questionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);

        QuestionBankVO questionBankVO = questionBankService.getQuestionBankVO(questionBank, request);

        // 判断是否需要关联查询表
        boolean needQueryQuestionList = questionBankQueryRequest.isNeedQueryQuestionList();
        if (needQueryQuestionList) {
            QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
            questionQueryRequest.setQuestionBankId(id);
            // TODO: 可以支持更多题目搜索参数, 例如分页
            Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
            Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage, request);
            questionBankVO.setQuestionPage(questionVOPage);
        }

//        JdHotKeyStore.smartSet(key, questionBankVO); // set cache(But the key must be a hot key.)

        // TODO: 可以只利用 JDHotKey 的探测功能，将查询数据库动作改为查询 Redis，这样就形成了多级缓存，而我们的 Redis 也可以做主动缓存
        // TODO: 可以降级运行
        // TODO: 增加堆热点题目的自动缓存
        // TODO: 编写注解，以 @Cacheable 注解来使用 AOP 快速扫描实现快速热点

        // 获取封装类
        return ResultUtils.success(questionBankVO);
    }

    /**
     * 分页获取题库列表（仅管理员可用）
     *
     * @param questionBankQueryRequest
     * @return
     */
    @PostMapping("/list/page")
//    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        return ResultUtils.success(questionBankPage);
    }

    /**
     * 分页获取题库列表（封装类）(flow control)
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
//    @SentinelResource(
//            value = "listQuestionBankVOByPage",
//            blockHandler = "handleBlockException",
//            fallback = "handleFallback"
//    )
    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest,
            HttpServletRequest request
    ) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();

        // 限制爬虫爬取的数量
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(
                new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest)
        );

        // 获取封装类
        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
    }

    // TODO: 最好是编写流量控制和熔断控制的类, 更加优雅

    /**
     * listQuestionBankVOByPage 对应流量机制(对 普通业务异常 + 内置熔断异常 进行处理)
     */
    public BaseResponse<Page<QuestionBankVO>> handleBlockException(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest,
            HttpServletRequest request,
            BlockException ex
    ) {
        // 熔断机制处理
        if (ex instanceof DegradeException) {
            return handleFallback(questionBankQueryRequest, request, ex);
        }

        // 流量机制处理
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统压力较大, 请稍后再尝试");
    }

    /**
     * listQuestionBankVOByPage 对应熔断机制(对 普通业务异常 进行处理)
     */
    public BaseResponse<Page<QuestionBankVO>> handleFallback(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest,
            HttpServletRequest request,
            Throwable ex
    ) {
        return ResultUtils.success(null); // TODO: 可以返回本地数据或空数据
    }

    /**
     * 分页获取当前登录用户创建的题库列表
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionBankQueryRequest.setUserId(loginUser.getId());
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
    }

    /**
     * 编辑题库（给用户使用）
     *
     * @param questionBankEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
//    @SaCheckRole(UserConstant.ADMIN_ROLE) // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest questionBankEditRequest, HttpServletRequest request) {

        if (questionBankEditRequest == null || questionBankEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankEditRequest, questionBank);

        // 数据校验
        questionBankService.validQuestionBank(questionBank, false);
        User loginUser = userService.getLoginUser(request);

        // 判断是否存在
        long id = questionBankEditRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
