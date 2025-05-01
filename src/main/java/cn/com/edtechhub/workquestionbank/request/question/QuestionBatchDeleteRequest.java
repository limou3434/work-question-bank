package cn.com.edtechhub.workquestionbank.request.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除题目请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Data
public class QuestionBatchDeleteRequest implements Serializable {

    /**
     * 题目 id 列表
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}
