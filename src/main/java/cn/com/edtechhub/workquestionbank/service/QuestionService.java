package cn.com.edtechhub.workquestionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.com.edtechhub.workquestionbank.request.question.QuestionQueryRequest;
import cn.com.edtechhub.workquestionbank.model.entity.Question;
import cn.com.edtechhub.workquestionbank.model.vo.QuestionVO;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目服务
 *
* @author <a href="https://github.com/limou3434">limou3434</a>
 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验数据
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 分页获取题目列表
     */
    Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest);

    /**
     * 从 ES 查询题目
     */
    Page<Question> searchFromEs(QuestionQueryRequest questionQueryRequest);

    /**
     * 批量删除题目
     */
    void batchDeleteQuestions(List<Long> questionIdList);
}
