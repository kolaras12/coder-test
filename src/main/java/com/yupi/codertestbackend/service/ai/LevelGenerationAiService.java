package com.yupi.codertestbackend.service.ai;

import com.yupi.codertestbackend.model.dto.ai.LevelGenerationResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

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
    @SystemMessage(fromResource = "prompts/level-generation-system.txt")
    @UserMessage("当前薪资：{{salary}}")
    LevelGenerationResponse generateLevel(Integer salary);
}
