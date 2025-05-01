package cn.com.edtechhub.workquestionbank.request.questionBank;

import cn.com.edtechhub.workquestionbank.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * 查询题库请求
 *
* @author <a href="https://github.com/limou3434">limou3434</a>
* @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 是否要关联查询题目列表(默认不查询)
     */
    private boolean needQueryQuestionList;

    private static final long serialVersionUID = 1L;
}
