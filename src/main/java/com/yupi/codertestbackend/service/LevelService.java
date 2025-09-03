package com.yupi.codertestbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.codertestbackend.model.entity.Level;

/**
 * 关卡服务
 */
public interface LevelService extends IService<Level> {

    /**
     * 根据用户薪资生成关卡
     *
     * @param salary 用户当前薪资
     * @return 生成的关卡
     */
    Level generateLevel(Integer salary);
}

