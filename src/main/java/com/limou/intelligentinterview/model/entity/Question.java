package com.limou.intelligentinterview.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题目
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID) // NOTE: 最好不要使用 IdType.AUTO, 否则有可能有爬取的风险(这里使用雪花递增算法, 生成长整型随机整数), 其他表也需要这样自动生成
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表(json 数组)
     */
    private String tags;

    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic // NOTE: 告知 MybatisX 该字段表示逻辑删除字段, 其他有的也要加上
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}