package cn.com.edtechhub.workquestionbank.request.questionBankQuestion;

import cn.com.edtechhub.workquestionbank.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询题库题目关联表请求
 *
* @author <a href="https://github.com/limou3434">limou3434</a>
* @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
