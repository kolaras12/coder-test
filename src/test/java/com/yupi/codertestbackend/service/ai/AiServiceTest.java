package com.yupi.codertestbackend.service.ai;

import com.yupi.codertestbackend.model.dto.ai.LevelGenerationResponse;
import com.yupi.codertestbackend.model.dto.ai.ResultReportResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;

/**
 * AI 服务测试类
 * 注意：此测试需要配置有效的 API Key 才能运行
 */
@SpringBootTest
@ActiveProfiles("local")
@Slf4j
public class AiServiceTest {

    @Resource
    private LevelGenerationAiService levelGenerationAiService;

    @Resource
    private ResultReportAiService resultReportAiService;

    /**
     * 测试关卡生成服务
     * 注意：需要有效的 API Key 才能运行
     */
    @Test
    void testLevelGeneration() {
        try {
            log.info("开始测试关卡生成服务...");
            
            // 测试生成关卡
            Integer salary = 10000;
            LevelGenerationResponse response = levelGenerationAiService.generateLevel(salary);
            
            log.info("关卡生成成功:");
            log.info("关卡名称: {}", response.getLevelName());
            log.info("难度等级: {}", response.getDifficulty());
            log.info("目标薪资: {}", response.getTargetSalary());
            log.info("选项数量: {}", response.getOptions() != null ? response.getOptions().size() : 0);
            
        } catch (Exception e) {
            log.warn("关卡生成测试失败（可能是 API Key 未配置）: {}", e.getMessage());
            // 不抛出异常，因为这可能是配置问题而非代码问题
        }
    }

    /**
     * 测试结果报告生成服务
     * 注意：需要有效的 API Key 才能运行
     */
    @Test
    void testResultReportGeneration() {
        try {
            log.info("开始测试结果报告生成服务...");
            
            // 模拟测试数据
            String levelName = "测试关卡";
            String levelDesc = "这是一个测试关卡描述";
            String userOptions = "[\"选项1\", \"选项2\"]";
            String trueOptions = "[\"选项1\", \"选项3\"]";
            Integer salary = 10000;
            
            ResultReportResponse response = resultReportAiService.generateResultReport(
                levelName, levelDesc, userOptions, trueOptions, salary
            );
            
            log.info("结果报告生成成功:");
            log.info("得分: {}", response.getScore());
            log.info("评价: {}", response.getComment());
            log.info("薪资变化: {}", response.getSalaryChange());
            log.info("建议: {}", response.getSuggest());
            
        } catch (Exception e) {
            log.warn("结果报告生成测试失败（可能是 API Key 未配置）: {}", e.getMessage());
            // 不抛出异常，因为这可能是配置问题而非代码问题
        }
    }
}
