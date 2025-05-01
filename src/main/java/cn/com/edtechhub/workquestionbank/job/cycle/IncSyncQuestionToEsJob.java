package cn.com.edtechhub.workquestionbank.job.cycle;

import cn.com.edtechhub.workquestionbank.mapper.QuestionEsMapper;
import cn.com.edtechhub.workquestionbank.mapper.QuestionMapper;
import cn.com.edtechhub.workquestionbank.model.dto.QuestionEsDTO;
import cn.com.edtechhub.workquestionbank.model.entity.Question;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IncSyncQuestionToEsJob {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionEsMapper questionEsMapper;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        long FIVE_MINUTES = 5 * 60 * 1000L;
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - FIVE_MINUTES);
        List<Question> questionList = questionMapper.listQuestionWithDelete(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(questionList)) {
            log.debug("IncSyncQuestionToEsJob no data...");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("IncSyncQuestionToEsJob start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsMapper.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("IncSyncQuestionToEsJob end, total {}", total);
    }
}
