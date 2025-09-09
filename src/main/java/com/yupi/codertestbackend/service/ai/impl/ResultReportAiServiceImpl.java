package com.yupi.codertestbackend.service.ai.impl;

import com.yupi.codertestbackend.model.dto.ai.ResultReportResponse;
import com.yupi.codertestbackend.service.ai.ResultReportAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * AI生成结果报告服务实现
 */
@Service
@Slf4j
public class ResultReportAiServiceImpl implements ResultReportAiService {

    @Override
    public ResultReportResponse generateResultReport(String levelName, String levelDesc, 
                                                   String userOptions, String trueOptions, Integer salary) {
        try {
            // TODO: 这里应该调用实际的AI服务（如阿里云DashScope）
            // 读取提示词文件并构建请求
            // String systemPrompt = readPromptFile("prompts/result-report-system.txt");
            
            // 现在先返回模拟数据
            log.info("模拟AI生成结果报告，关卡：{}，薪资：{}", levelName, salary);
            
            return createMockReport(levelName, userOptions, trueOptions, salary);
            
        } catch (Exception e) {
            log.error("生成结果报告失败：{}", e.getMessage(), e);
            throw new RuntimeException("AI服务调用失败", e);
        }
    }
    
    
    /**
     * 创建模拟报告（用于测试）
     */
    private ResultReportResponse createMockReport(String levelName, String userOptions, 
                                                String trueOptions, Integer salary) {
        ResultReportResponse response = new ResultReportResponse();
        
        // 模拟评分逻辑
        int score = calculateMockScore(userOptions, trueOptions);
        response.setScore(score);
        
        // 根据分数生成评价和薪资变化
        if (score >= 90) {
            response.setComment("恭喜！你的表现非常出色，可以考虑升职加薪了！");
            response.setSalaryChange(1000);
            response.setSuggest("建议投递：阿巴阿巴集团、企鹅大王科技、字跳跳公司等一线互联网公司");
        } else if (score >= 70) {
            response.setComment("表现不错，继续努力就能更上一层楼！");
            response.setSalaryChange(500);
            response.setSuggest("建议投递：美团外卖、滴滴出行、小红薯等知名互联网公司");
        } else if (score >= 50) {
            response.setComment("还需要继续学习提升，加油！");
            response.setSalaryChange(0);
            response.setSuggest("建议先在当前公司积累经验，或考虑一些成长型公司");
        } else {
            response.setComment("需要重新审视自己的技术栈，建议多学习基础知识");
            response.setSalaryChange(-500);
            response.setSuggest("建议考虑培训机构或者初创公司积累经验");
        }
        
        response.setReason(String.format("根据你在%s关卡的表现，得分%d分，主要考虑了技术选型的准确性和实现方案的合理性。", 
                levelName, score));
        
        // 设置正确选项
        response.setTrueOptions(Arrays.asList("Spring Boot框架", "MySQL数据库", "Redis缓存"));
        
        // 设置标准答案解析
        response.setStandardAnswer(generateStandardAnswer(levelName));
        
        return response;
    }
    
    /**
     * 模拟计算分数
     */
    private int calculateMockScore(String userOptions, String trueOptions) {
        // 简单的模拟评分逻辑
        try {
            // 这里可以解析JSON并进行对比
            // 现在简化处理，随机生成一个合理的分数
            return 60 + (int)(Math.random() * 35); // 60-95分之间
        } catch (Exception e) {
            return 60; // 默认及格分
        }
    }
    
    /**
     * 生成标准答案解析
     */
    private String generateStandardAnswer(String levelName) {
        return String.format("""
                ## %s - 标准解决方案
                
                ### 技术架构设计
                
                在企业级开发中，这类项目通常采用分层架构设计，包括表现层、业务逻辑层、数据访问层。
                
                **核心技术选型：**
                - **后端框架**：Spring Boot提供了快速开发能力，内置了大量企业级特性
                - **数据库**：MySQL作为主流关系型数据库，提供ACID特性保证数据一致性
                - **缓存层**：Redis用于提升系统性能，减少数据库压力
                
                ### 实现关键点
                
                1. **数据库设计**：合理的表结构设计，建立适当的索引提升查询效率
                2. **接口设计**：RESTful API设计，保证接口的可维护性和扩展性
                3. **安全控制**：实现用户认证授权，防止SQL注入等安全问题
                4. **性能优化**：通过缓存、分页、异步处理等方式提升系统性能
                
                ### 部署运维
                
                采用Docker容器化部署，配合CI/CD流水线实现自动化部署和运维监控。
                
                这样的技术方案既保证了系统的稳定性，又具备了良好的可扩展性，是企业级项目的标准实践。
                """, levelName);
    }
}
