package cn.com.edtechhub.workquestionbank.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.edtechhub.workquestionbank.common.ErrorCode;
import cn.com.edtechhub.workquestionbank.constant.CommonConstant;
import cn.com.edtechhub.workquestionbank.exception.BusinessException;
import cn.com.edtechhub.workquestionbank.exception.ThrowUtils;
import cn.com.edtechhub.workquestionbank.mapper.QuestionBankQuestionMapper;
import cn.com.edtechhub.workquestionbank.request.questionBankQuestion.QuestionBankQuestionQueryRequest;
import cn.com.edtechhub.workquestionbank.model.entity.Question;
import cn.com.edtechhub.workquestionbank.model.entity.QuestionBank;
import cn.com.edtechhub.workquestionbank.model.entity.QuestionBankQuestion;
import cn.com.edtechhub.workquestionbank.model.entity.User;
import cn.com.edtechhub.workquestionbank.model.vo.QuestionBankQuestionVO;
import cn.com.edtechhub.workquestionbank.model.vo.UserVO;
import cn.com.edtechhub.workquestionbank.service.QuestionBankQuestionService;
import cn.com.edtechhub.workquestionbank.service.QuestionBankService;
import cn.com.edtechhub.workquestionbank.service.QuestionService;
import cn.com.edtechhub.workquestionbank.service.UserService;
import cn.com.edtechhub.workquestionbank.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 题库题目关联表服务实现
 *
* @author <a href="https://github.com/limou3434">limou3434</a>
* @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy // 这里必须引入懒加载, 由于前面编写策略的原因, 会出现循环依赖
    private QuestionService questionService;

    @Resource
    @Lazy
    private QuestionBankService questionBankService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);

        // 题目必须存在
        Long questionId = questionBankQuestion.getQuestionId();
        if(questionId != null) {
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 题库必须存在
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if(questionBankId != null) {
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();

        // todo 补充需要的查询条件
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联表封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态(暂时不用)
        // endregion

        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联表封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态(暂时不需要)
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    @Override
    // @Transactional(rollbackFor = Exception.class) // 管理数据库事务回滚(默认运行时异常回滚, 但是这里设置了全部异常都回滚, 使用第三种方法时就可以取消这个注解)
    public void batchAddQuestionsToBank(List<Long> questionIdList, long questionBankId, User loginUser) {
        // 1.最直接粗暴的写法(效率低下, 修改缓慢, 异常颗粒大)
        /*
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目标识列表不能空传递");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库标识不能空传递"); // TODO: 感觉可以稍微修改一下, 怪怪的
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户尚未登陆");

        // 检查题目 id 是否存在
        List<Question> questionList = questionService.listByIds(questionIdList); // 得到合法的题目实体列表
        List<Long> validQuestionIdList = questionList.stream() // 转化为流处理对象
                .map(Question::getId) // 滤得所有合法题目标识
                .collect(Collectors.toList()); // 得到合法的题目标识列表

        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法题目列表最终结果为空，视为无效操作");

        // 检查题库 id 是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库标识不存在");

        // 向题库中绑定批量的题目
        for(long questionId : validQuestionIdList) {
            // 检查是否存在题目题库关联记录
            boolean exists = this.lambdaQuery()
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .exists();

            // 如果关系记录存在, 跳过绑定
            if (exists) {
                continue;
            }

            QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
            questionBankQuestion.setQuestionBankId(questionBankId);
            questionBankQuestion.setQuestionId(questionId);
            questionBankQuestion.setUserId(loginUser.getId());
            boolean result = this.save(questionBankQuestion);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "题目绑定失败");
        }
        */

        // 2.最直接优化的写法(效率低下, 修改缓慢, 异常颗粒小)
        /*
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目标识列表不能空传递");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库标识不能空传递"); // TODO: 感觉可以稍微修改一下, 怪怪的
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户尚未登陆");

        // 合法校验: 检查题目 id 是否存在
        List<Question> questionList = questionService.listByIds(questionIdList); // 得到合法的题目实体列表
        List<Long> validQuestionIdList = questionList.stream() // 转化为流处理对象
                .map(Question::getId) // 滤得所有合法题目标识
                .collect(Collectors.toList()); // 得到合法的题目标识列表
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法题目列表最终结果为空，视为无效操作");

        // 合法校验: 检查题库 id 是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库标识不存在");

        // 实际操作: 向题库中绑定批量的题目
        for(long questionId : validQuestionIdList) {
            // 如果题目题库关联记录记录为存在则跳过绑定
            boolean exists = this.lambdaQuery()
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .exists();
            if (exists) {
                continue;
            }

            // 如果题目题库关联记录记录不存在则开始绑定
            QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
            questionBankQuestion.setQuestionBankId(questionBankId);
            questionBankQuestion.setQuestionId(questionId);
            questionBankQuestion.setUserId(loginUser.getId());

            try {
                boolean result = this.save(questionBankQuestion);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "题目绑定失败");
            } catch (DataAccessException e) {
                log.error("数据库连接问题、事务问题等导致操作失败, 题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库本身操作失败");
            } catch (Exception e) {
                // 捕获其他异常，做通用处理
                log.error("添加题目到题库时发生未知错误, 题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库绑定题目失败, 发生未知错误");
            }
        }
        */

        // 3.最直接优化的写法(效率较下, 修改缓慢, 异常颗粒小, 稳定度高)
        /*
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目标识列表不能空传递");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库标识不能空传递"); // TODO: 感觉可以稍微修改一下, 怪怪的
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户尚未登陆");

        // 合法校验: 检查题目 id 是否存在
        List<Question> questionList = questionService.listByIds(questionIdList); // 得到合法的题目实体列表
        List<Long> validQuestionIdList = questionList.stream() // 转化为流处理对象
                .map(Question::getId) // 滤得所有合法题目标识
                .collect(Collectors.toList()); // 得到合法的题目标识列表
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法题目列表最终结果为空，视为无效操作");

        // 合法校验: 检查题库 id 是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库标识不存在");

        // 实际操作: 向题库中绑定批量的题目(处理为批量短事务)
        int batchSize = 1000; // 理论上每次短事务中最多处理的个数
        int totalQuestionListSize = questionList.size(); // 实际上需要处理的个数
        for (int i = 0; i < totalQuestionListSize; i += batchSize) {
            // 生成每批次的数据
            List<Long> subList = validQuestionIdList.subList(i, Math.min(i + batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestions = subList.stream()
                    .map(questionId -> {
                        // 如果题目题库关联记录记录为存在则无需后续绑定
                        boolean exists = this.lambdaQuery()
                                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                                .eq(QuestionBankQuestion::getQuestionId, questionId)
                                .exists();
                        if (exists) {
                            return null; // TODO: 临时处理
                        }

                        // 如果题目题库关联记录记录不存在则需要后续绑定
                        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                        questionBankQuestion.setQuestionBankId(questionBankId);
                        questionBankQuestion.setQuestionId(questionId);
                        questionBankQuestion.setUserId(loginUser.getId());
                        return questionBankQuestion;
                    })
                    .filter(Objects::nonNull)  // 过滤掉 null 值
                    .collect(Collectors.toList());

            // 处理每批次的数据 // TODO: 这里关于代理的问题需要深入了解一下
            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy(); // 获取代理
            questionBankQuestionService.batchAddQuestionsToBankInner(questionBankQuestions);
        }
        */

        // 5.优化 SQL
        /*
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目标识列表不能空传递");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库标识不能空传递"); // TODO: 感觉可以稍微修改一下, 怪怪的
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户尚未登陆");

        // 合法校验: 检查题目 id 是否存在
        LambdaQueryWrapper<Question> queryLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList); // NOTE: 优化
        List<Long> validQuestionIdList = questionService.listObjs(queryLambdaQueryWrapper, obj -> (Long) obj); // 得到合法的题目实体标识
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法题目列表最终结果为空，视为无效操作");

        // 合法校验: 检查题库 id 是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库标识不存在");

        // 实际操作: 向题库中绑定批量的题目(处理为批量短事务)
        int batchSize = 1000; // 理论上每次短事务中最多处理的个数
        int totalQuestionListSize = validQuestionIdList.size(); // 实际上需要处理的个数
        for (int i = 0; i < totalQuestionListSize; i += batchSize) {
            // 生成每批次的数据
            List<Long> subList = validQuestionIdList.subList(i, Math.min(i + batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestions = subList.stream()
                    .map(questionId -> {
                        // 如果题目题库关联记录记录为存在则无需后续绑定
                        boolean exists = this.lambdaQuery()
                                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                                .eq(QuestionBankQuestion::getQuestionId, questionId)
                                .exists();
                        if (exists) {
                            return null; // TODO: 临时处理
                        }

                        // 如果题目题库关联记录记录不存在则需要后续绑定
                        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                        questionBankQuestion.setQuestionBankId(questionBankId);
                        questionBankQuestion.setQuestionId(questionId);
                        questionBankQuestion.setUserId(loginUser.getId());
                        return questionBankQuestion;
                    })
                    .filter(Objects::nonNull)  // 过滤掉 null 值
                    .collect(Collectors.toList());

            // 处理每批次的数据 // TODO: 这里关于代理的问题需要深入了解一下
            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy(); // 获取代理
            questionBankQuestionService.batchAddQuestionsToBankInner(questionBankQuestions);
        }
        */

        // 6.线程池优化
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目标识列表不能空传递");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库标识不能空传递"); // TODO: 感觉可以稍微修改一下, 怪怪的
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户尚未登陆");

        // 合法校验: 检查题目 id 是否存在
        LambdaQueryWrapper<Question> queryLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList); // NOTE: 优化
        List<Long> validQuestionIdList = questionService.listObjs(queryLambdaQueryWrapper, obj -> (Long) obj); // 得到合法的题目实体标识
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法题目列表最终结果为空，视为无效操作");

        // 合法校验: 检查题库 id 是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库标识不存在");

        // 自定义线程池(IO 密集型)
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                20, // 核心线程数
                50, // 最大线程数
                60L, // 空闲存活时间大小
                TimeUnit.SECONDS, // 空闲存活时间单位
                new LinkedBlockingQueue<>(10000), // 阻塞容量
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略(在 CallerRunsPolicy 策略下, 任务不会被丢弃, 也不会抛出异常, 而是会让提交任务的线程直接执行这个任务, 而不是让线程池中的线程去执行)
        );

        // 保存所有批次任务的列表
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 实际操作: 向题库中绑定批量的题目(处理为批量短事务)
        int batchSize = 1000; // 理论上每次短事务中最多处理的个数
        int totalQuestionListSize = validQuestionIdList.size(); // 实际上需要处理的个数
        for (int i = 0; i < totalQuestionListSize; i += batchSize) {
            // 生成每批次的数据
            List<Long> subList = validQuestionIdList.subList(i, Math.min(i + batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestions = subList.stream()
                    .map(questionId -> {
                        // 如果题目题库关联记录记录为存在则无需后续绑定
                        boolean exists = this.lambdaQuery()
                                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                                .eq(QuestionBankQuestion::getQuestionId, questionId)
                                .exists();
                        if (exists) {
                            return null; // TODO: 临时处理
                        }

                        // 如果题目题库关联记录记录不存在则需要后续绑定
                        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                        questionBankQuestion.setQuestionBankId(questionBankId);
                        questionBankQuestion.setQuestionId(questionId);
                        questionBankQuestion.setUserId(loginUser.getId());
                        return questionBankQuestion;
                    })
                    .filter(Objects::nonNull)  // 过滤掉 null 值
                    .collect(Collectors.toList());

            // 处理每批次的数据 // TODO: 这里关于代理的问题需要深入了解一下
            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy(); // 获取代理

            // 将任务放置到异步列表中
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // 异步执行任务
                questionBankQuestionService.batchAddQuestionsToBankInner(questionBankQuestions);
            }, customExecutor).exceptionally(ex -> {
                log.error("异步执行任务失败");
                return null; // 避免中断其他异步任务的执行
            });
            futures.add(future);
        }

        // 阻塞等待所有任务执行完毕(因此还不是完全的异步, 可以在执行期间直接返回/欺骗前端已经提交任务的id, 然后根据定时任务或消息队列选择之前提交好的任务进行处理, 而前端可以通过 轮询调用接口/WebSocket/SSE 的方式得知任务的实际进度m 比如提供一个任务 id 查询状态的接口)
        CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .join();

        // 释放线程池
        customExecutor.shutdown();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestionList) {
        /*
        for(QuestionBankQuestion questionBankQuestion : questionBankQuestionList) {
            Long questionId = questionBankQuestion.getQuestionId();
            Long questionBankId = questionBankQuestion.getQuestionBankId();
            try {
                boolean result = this.save(questionBankQuestion);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "题目绑定失败");
            } catch (DataAccessException e) {
                log.error("数据库连接问题、事务问题等导致操作失败, 题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库本身操作失败");
            } catch (Exception e) {
                // 捕获其他异常，做通用处理
                log.error("添加题目到题库时发生未知错误, 题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库绑定题目失败, 发生未知错误");
            }
        }
        */

        // 5.最效率的写法(效率较好, 修改缓慢, 异常颗粒小, 稳定度高)
        try {
            boolean result = this.saveBatch(questionBankQuestionList);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "题目绑定失败");
        } catch (DataAccessException e) {
            // log.error("数据库连接问题、事务问题等导致操作失败, 题目 id: {}, 题库 id: {}, 错误信息: {}", questionId, questionBankId, e.getMessage());
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库本身操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            // log.error("添加题目到题库时发生未知错误, 题目 id: {}, 题库 id: {}, 错误信息: {}", questionId, questionBankId, e.getMessage());
             log.error("添加题目到题库时发生未知错误, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库绑定题目失败, 发生未知错误");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 管理数据库事务回滚(默认运行时异常回滚, 但是这里设置了全部异常都回滚)
    public void batchRemoveQuestionsFromBank(List<Long> questionIdList, long questionBankId) {
        /*
        // 1.最直接粗暴的写法(效率低下, 修改缓慢, 异常颗粒大)
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目标识列表不能空传递");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库标识不能空传递"); // TODO: 感觉可以稍微修改一下, 怪怪的

        // 向题库中解绑批量的题目
        for(long questionId : questionIdList) {
            // 检查是否存在题目题库关联记录
             LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);

            // 如果关系记录不存在, 跳过解绑
            boolean exists = this.count(lambdaQueryWrapper) > 0;
            if (!exists) {
                continue;
            }

            // 题目解绑题库关系
            boolean result = this.remove(lambdaQueryWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "题目解绑失败");
        }
        */

        // 2.最直接粗暴的写法(效率低下, 修改缓慢, 异常颗粒小)
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目标识列表不能空传递");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库标识不能空传递"); // TODO: 感觉可以稍微修改一下, 怪怪的

        // TODO: 不知道需不需要做非法校验

        // 实际操作: 向题库中解绑批量的题目
        for(long questionId : questionIdList) {
            // 如果题目题库关联记录记录不存在则跳过解绑
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            boolean exists = this.count(lambdaQueryWrapper) > 0;
            if (!exists) {
                continue;
            }

            // 如果题目题库关联记录记录为存在则开始解绑
            try {
                boolean result = this.remove(lambdaQueryWrapper);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "题目解绑失败");
            } catch (DataAccessException e) {
                log.error("数据库连接问题、事务问题等导致操作失败, 题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库本身操作失败");
            } catch (Exception e) {
                // 捕获其他异常，做通用处理
                log.error("添加题目到题库时发生未知错误, 题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库绑定题目失败, 发生未知错误");
            }
        }
    }

}

// TODO: 在两个管理员同时添加题目到题库中可能需要分布式锁(或者版本乐观锁), 不过由于本项目有主键约束, 并且重复添加是被忽略的, 因此不会发生冲突, 对于管理员用户来说绑定题目到题库是透明的
// TODO: 返回值优化, 绑定或解绑来说, 不要直接返回 void 而是具体的标识值
// TODO: 不同方案的性能比较
