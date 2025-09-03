package com.yupi.codertestbackend.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.codertestbackend.mapper.UserLevelMapper;
import com.yupi.codertestbackend.model.dto.level.SubmitAnswerRequest;
import com.yupi.codertestbackend.model.entity.Level;
import com.yupi.codertestbackend.model.entity.User;
import com.yupi.codertestbackend.model.entity.UserLevel;
import com.yupi.codertestbackend.service.LevelService;
import com.yupi.codertestbackend.service.UserLevelService;
import com.yupi.codertestbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户关卡服务实现
 */
@Service
@Slf4j
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevel> implements UserLevelService {

    @Resource
    private LevelService levelService;

    @Resource
    private UserService userService;

    @Override
    public UserLevel submitAnswer(SubmitAnswerRequest submitAnswerRequest, String userId) {
        String levelId = submitAnswerRequest.getLevelId();
        List<String> userOptions = submitAnswerRequest.getUserOptions();

        if (StrUtil.isBlank(levelId) || StrUtil.isBlank(userId)) {
            throw new RuntimeException("参数不能为空");
        }

        // 获取关卡信息
        Level level = levelService.getById(levelId);
        if (level == null) {
            throw new RuntimeException("关卡不存在");
        }

        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // TODO: 这里需要调用AI来生成结果报告
        // 1. 解析关卡的正确答案
        // 2. 对比用户选择的答案
        // 3. 调用AI生成详细的结果报告
        // 4. 更新用户薪资
        
        log.info("用户 {} 提交关卡 {} 的答案：{}", userId, levelId, userOptions);
        
        // 临时生成示例结果（实际需要AI生成）
        UserLevel userLevel = new UserLevel();
        userLevel.setUserId(userId);
        userLevel.setLevelId(levelId);
        userLevel.setUserOptions(JSONUtil.toJsonStr(userOptions));
        userLevel.setScore(80); // 示例分数
        userLevel.setComment("答得不错！");
        userLevel.setSalaryChange(500); // 示例薪资变化
        userLevel.setSuggest("可以考虑投递一些中级岗位");
        userLevel.setReason("选择了大部分正确答案");
        userLevel.setTrueOptions("[]"); // JSON格式的正确答案
        userLevel.setStandardAnswer("这是标准答案解析");

        // 保存结果
        this.save(userLevel);

        // 更新用户薪资
        userService.updateUserSalary(userId, userLevel.getSalaryChange());

        return userLevel;
    }

    @Override
    public List<UserLevel> getUserLevelHistory(String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new RuntimeException("用户ID不能为空");
        }

        QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.orderByDesc("createTime");

        return this.list(queryWrapper);
    }
}

