package cn.com.edtechhub.workquestionbank.job.once;

import cn.com.edtechhub.workquestionbank.mapper.QuestionEsMapper;
import cn.com.edtechhub.workquestionbank.model.dto.QuestionEsDTO;
import cn.com.edtechhub.workquestionbank.model.entity.Question;
import cn.com.edtechhub.workquestionbank.service.QuestionService;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FullSyncQuestionToEsJob implements CommandLineRunner {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionEsMapper questionEsMapper;

    @Override
    public void run(String... args) {
        // 全量获取题目（数据量不大的情况下使用）
        List<Question> questionList = questionService.list();
        if (CollUtil.isEmpty(questionList)) {
            return;
        }
        // 转为 ES 实体类
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        // 分页批量插入到 ES
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.debug("FullSyncQuestionToEsJob start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            // 注意同步的数据下标不能超过总数据量
            int end = Math.min(i + pageSize, total);
            log.debug("sync from {} to {}", i, end);
            questionEsMapper.saveAll(questionEsDTOList.subList(i, end));
        }
        log.debug("FullSyncQuestionToEsJob end, total {}", total);
    }
}
