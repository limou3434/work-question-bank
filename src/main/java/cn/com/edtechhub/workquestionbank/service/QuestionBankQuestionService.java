package cn.com.edtechhub.workquestionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.com.edtechhub.workquestionbank.request.questionBankQuestion.QuestionBankQuestionQueryRequest;
import cn.com.edtechhub.workquestionbank.model.entity.QuestionBankQuestion;
import cn.com.edtechhub.workquestionbank.model.entity.User;
import cn.com.edtechhub.workquestionbank.model.vo.QuestionBankQuestionVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联表服务
 *
* @author <a href="https://github.com/limou3434">limou3434</a>
* @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);

    /**
     * 获取题库题目关联表封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题库题目关联表封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);

    /**
     * 向题库中批量绑定题目
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     */
    void batchAddQuestionsToBank(List<Long> questionIdList, long questionBankId, User loginUser);

    /**
     * 批量绑定题目的题库(内部调用, 短事务处理)
     *
     * @param questionBankQuestionList
     */

    @Transactional(rollbackFor = Exception.class) // 管理数据库事务回滚(默认运行时异常回滚, 但是这里设置了全部异常都回滚)
    void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestionList);

    /**
     * 向题库中批量解绑题目
     * @param questionIdList
     * @param questionBankId
     */
    void batchRemoveQuestionsFromBank(List<Long> questionIdList, long questionBankId);

}
