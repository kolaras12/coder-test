package com.yupi.codertestbackend.service.ai;

import com.yupi.codertestbackend.model.dto.ai.ResultReportResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI生成结果报告服务
 */
public interface ResultReportAiService {

    /**
     * 根据关卡信息和用户作答生成结果报告
     * 
     * @param levelName 关卡名称
     * @param levelDesc 关卡需求描述
     * @param userOptions 用户选择的选项（JSON字符串）
     * @param trueOptions 本关卡的正确选项（JSON字符串）
     * @param salary 用户当前薪资
     * @return 生成的结果报告
     */
    @SystemMessage(fromResource = "prompts/result-report-system.txt")
    @UserMessage("### 关卡名称\n{{levelName}}\n\n## 关卡的需求描述\n{{levelDesc}}\n\n## 用户选择的选项\n{{userOptions}}\n\n### 本关卡的正确选项\n{{trueOptions}}\n\n### 用户当前的薪资\n{{salary}}")
    ResultReportResponse generateResultReport(String levelName, String levelDesc, 
                                            String userOptions, String trueOptions, Integer salary);
}
