package cn.com.edtechhub.workquestionbank.mapper;

import cn.com.edtechhub.workquestionbank.model.dto.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 题目 ES 操作
 */
public interface QuestionEsMapper extends ElasticsearchRepository<QuestionEsDTO, Long> {

}
