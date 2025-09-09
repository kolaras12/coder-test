package com.yupi.codertestbackend.service.ai;

import com.yupi.codertestbackend.model.dto.ai.LevelGenerationResponse;

/**
 * AI生成关卡服务
 */
public interface LevelGenerationAiService {

    /**
     * 根据用户薪资生成关卡
     * 
     * @param salary 用户当前薪资
     * @return 生成的关卡信息
     */
    LevelGenerationResponse generateLevel(Integer salary);
}
