package cn.com.edtechhub.workquestionbank.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * 被删除对象的 ID
     */
    private Long id;

    /**
     * 版本控制标识符(暂时不纠结有什么用)
     */
    private static final long serialVersionUID = 1L;
}
