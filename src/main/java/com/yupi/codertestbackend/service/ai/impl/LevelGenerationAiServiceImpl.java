package com.yupi.codertestbackend.service.ai.impl;

import com.yupi.codertestbackend.model.dto.ai.LevelGenerationResponse;
import com.yupi.codertestbackend.service.ai.LevelGenerationAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * AI生成关卡服务实现
 */
@Service
@Slf4j
public class LevelGenerationAiServiceImpl implements LevelGenerationAiService {

    @Override
    public LevelGenerationResponse generateLevel(Integer salary) {
        try {
            // TODO: 这里应该调用实际的AI服务（如阿里云DashScope）
            // 读取提示词文件并构建请求
            // String systemPrompt = readPromptFile("prompts/level-generation-system.txt");
            // String userPrompt = "当前薪资：" + salary;
            
            // 现在先返回模拟数据
            log.info("模拟AI生成关卡，薪资：{}", salary);
            
            return createMockResponse(salary);
            
        } catch (Exception e) {
            log.error("生成关卡失败：{}", e.getMessage(), e);
            throw new RuntimeException("AI服务调用失败", e);
        }
    }
    
    
    /**
     * 创建模拟响应（用于测试）
     */
    private LevelGenerationResponse createMockResponse(Integer salary) {
        // 根据薪资创建不同难度的模拟关卡
        LevelGenerationResponse response = new LevelGenerationResponse();
        
        if (salary <= 8000) {
            response.setLevelName("基础Web开发项目");
            response.setLevelDesc("开发一个简单的博客系统，需要支持用户注册登录、文章发布、评论功能。要求界面简洁，功能完整。");
            response.setDifficulty("简单");
        } else if (salary <= 15000) {
            response.setLevelName("电商平台核心模块设计");
            response.setLevelDesc("设计一个电商平台的订单管理系统，需要处理高并发下单、库存管理、支付集成等核心功能。");
            response.setDifficulty("中等");
        } else {
            response.setLevelName("分布式微服务架构设计");
            response.setLevelDesc("设计一个大型互联网公司的用户中心微服务架构，需要考虑服务拆分、数据一致性、容灾备份等问题。");
            response.setDifficulty("困难");
        }
        
        // 设置选项（这里简化处理）
        response.setOptions(createMockOptions());
        response.setTargetSalary(salary);
        
        return response;
    }
    
    /**
     * 创建模拟选项
     */
    private java.util.List<com.yupi.codertestbackend.model.dto.ai.LevelOption> createMockOptions() {
        java.util.List<com.yupi.codertestbackend.model.dto.ai.LevelOption> options = new java.util.ArrayList<>();
        
        // 正确选项
        com.yupi.codertestbackend.model.dto.ai.LevelOption option1 = new com.yupi.codertestbackend.model.dto.ai.LevelOption();
        option1.setOptionName("Spring Boot框架");
        option1.setTrueAnswer(true);
        options.add(option1);
        
        com.yupi.codertestbackend.model.dto.ai.LevelOption option2 = new com.yupi.codertestbackend.model.dto.ai.LevelOption();
        option2.setOptionName("MySQL数据库");
        option2.setTrueAnswer(true);
        options.add(option2);
        
        // 干扰选项
        com.yupi.codertestbackend.model.dto.ai.LevelOption option3 = new com.yupi.codertestbackend.model.dto.ai.LevelOption();
        option3.setOptionName("汇编语言");
        option3.setTrueAnswer(false);
        options.add(option3);
        
        return options;
    }
}
