package com.limou.intelligentinterview.mapper;

import com.limou.intelligentinterview.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author ljp
* @description 针对表【question(题目)】的数据库操作 Mapper
* @createDate 2024-10-19 21:04:50
* @Entity com.limou.intelligentinterview.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {
    /**
     * 查询题目列表（包括已被删除的数据）
     */
    @Select("select * from question where updateTime >= #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);

}




