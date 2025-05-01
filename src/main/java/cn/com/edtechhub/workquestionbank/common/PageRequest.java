package cn.com.edtechhub.workquestionbank.common;

import cn.com.edtechhub.workquestionbank.constant.CommonConstant;
import lombok.Data;

/**
 * 分页请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC; // 这个 CommonConstant 枚举体其实就定义了两个枚举值(升序和降序)

}
