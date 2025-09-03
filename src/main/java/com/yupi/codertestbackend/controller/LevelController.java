package com.yupi.codertestbackend.controller;

import com.yupi.codertestbackend.model.dto.level.GenerateLevelRequest;
import com.yupi.codertestbackend.model.entity.Level;
import com.yupi.codertestbackend.model.entity.User;
import com.yupi.codertestbackend.model.vo.BaseResponse;
import com.yupi.codertestbackend.model.vo.ResultUtils;
import com.yupi.codertestbackend.service.LevelService;
import com.yupi.codertestbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 关卡接口
 */
@RestController
@RequestMapping("/level")
@Slf4j
@Tag(name = "关卡接口")
public class LevelController {

    @Resource
    private LevelService levelService;

    @Resource
    private UserService userService;

    /**
     * 生成关卡
     *
     * @param generateLevelRequest 生成关卡请求体
     * @param request              请求对象
     * @return 生成的关卡
     */
    @PostMapping("/generate")
    @Operation(summary = "生成关卡")
    public BaseResponse<Level> generateLevel(@RequestBody GenerateLevelRequest generateLevelRequest, 
                                           HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User user = userService.getLoginUser(request);
            
            // 使用用户当前薪资或请求中的薪资
            Integer salary = generateLevelRequest.getSalary();
            if (salary == null) {
                salary = user.getSalary();
            }
            
            Level level = levelService.generateLevel(salary);
            return ResultUtils.success(level);
        } catch (Exception e) {
            log.error("生成关卡失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }

    /**
     * 获取关卡详情
     *
     * @param id 关卡ID
     * @return 关卡详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取关卡详情")
    public BaseResponse<Level> getLevelById(@PathVariable String id) {
        try {
            Level level = levelService.getById(id);
            if (level == null) {
                return ResultUtils.error(40400, "关卡不存在");
            }
            return ResultUtils.success(level);
        } catch (Exception e) {
            log.error("获取关卡详情失败", e);
            return ResultUtils.error(50000, e.getMessage());
        }
    }
}

