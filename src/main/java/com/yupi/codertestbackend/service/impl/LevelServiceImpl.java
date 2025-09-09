package com.yupi.codertestbackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.codertestbackend.mapper.LevelMapper;
import com.yupi.codertestbackend.model.dto.ai.LevelGenerationResponse;
import com.yupi.codertestbackend.model.entity.Level;
import com.yupi.codertestbackend.service.LevelService;
import com.yupi.codertestbackend.service.ai.LevelGenerationAiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 关卡服务实现
 */
@Service
@Slf4j
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level> implements LevelService {

    @Resource
    private LevelGenerationAiService levelGenerationAiService;

    @Override
    public Level generateLevel(Integer salary) {
        log.info("开始生成关卡，用户薪资：{}", salary);
        
        try {
            // 调用AI生成关卡
            LevelGenerationResponse aiResponse = levelGenerationAiService.generateLevel(salary);
            
            // 将AI响应转换为Level实体
            Level level = new Level();
            level.setLevelName(aiResponse.getLevelName());
            level.setLevelDesc(aiResponse.getLevelDesc());
            level.setOptions(JSONUtil.toJsonStr(aiResponse.getOptions())); // 将选项列表转换为JSON字符串
            level.setDifficulty(aiResponse.getDifficulty());
            level.setTargetSalary(aiResponse.getTargetSalary());
            
            // 保存到数据库
            boolean saveResult = this.save(level);
            if (!saveResult) {
                throw new RuntimeException("保存关卡失败");
            }
            
            log.info("关卡生成成功，关卡ID：{}，关卡名称：{}", level.getId(), level.getLevelName());
            return level;
            
        } catch (Exception e) {
            log.error("AI生成关卡失败，用户薪资：{}，错误信息：{}", salary, e.getMessage(), e);
            
            // 降级处理：返回一个默认关卡
            Level fallbackLevel = createFallbackLevel(salary);
            this.save(fallbackLevel);
            
            log.warn("使用降级关卡，关卡ID：{}", fallbackLevel.getId());
            return fallbackLevel;
        }
    }
    
    /**
     * 创建降级关卡（当AI服务不可用时使用）
     */
    private Level createFallbackLevel(Integer salary) {
        Level level = new Level();
        level.setLevelName("系统维护中的临时关卡");
        level.setLevelDesc("抱歉，AI服务暂时不可用，这是一个临时关卡。请稍后再试。");
        level.setOptions("[]"); // 空选项
        level.setDifficulty(getDifficultyBySalary(salary));
        level.setTargetSalary(salary);
        return level;
    }
    
    /**
     * 根据薪资确定难度等级
     */
    private String getDifficultyBySalary(Integer salary) {
        if (salary <= 8000) {
            return "简单";
        } else if (salary <= 15000) {
            return "中等";
        } else {
            return "困难";
        }
    }
}

