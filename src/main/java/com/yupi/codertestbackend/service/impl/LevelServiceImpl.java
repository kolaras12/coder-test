package com.yupi.codertestbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.codertestbackend.mapper.LevelMapper;
import com.yupi.codertestbackend.model.entity.Level;
import com.yupi.codertestbackend.service.LevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 关卡服务实现
 */
@Service
@Slf4j
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level> implements LevelService {

    @Override
    public Level generateLevel(Integer salary) {
        // TODO: 这里需要调用AI来生成关卡
        // 1. 根据薪资确定难度等级
        // 2. 调用AI生成关卡内容
        // 3. 保存关卡到数据库
        
        log.info("生成关卡，用户薪资：{}", salary);
        
        // 临时返回一个示例关卡（实际需要AI生成）
        Level level = new Level();
        level.setLevelName("示例关卡");
        level.setLevelDesc("这是一个示例关卡描述");
        level.setOptions("[]"); // JSON格式的选项
        level.setDifficulty(getDifficultyBySalary(salary));
        level.setTargetSalary(salary);
        
        // 保存到数据库
        this.save(level);
        
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

