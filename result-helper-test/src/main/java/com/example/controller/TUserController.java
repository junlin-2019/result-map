package com.example.controller;

import com.example.entity.TUser;
import com.example.plugins.ValueMap;
import com.example.service.TUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * (TUser)表控制层
 *
 * @author makejava
 * @since 2020-09-28 18:22:57
 */
@RestController
@RequestMapping("tUser")
public class TUserController {
    /**
     * 服务对象
     */
    @Resource
    private TUserService tUserService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public Map selectOne(Integer id) {
        return this.tUserService.queryById(id);
    }


    @GetMapping("selectAll")
    public ValueMap<Integer, String> selectAll() {
        return this.tUserService.queryAll();
    }

    @GetMapping("selectAll1")
    public List<TUser> selectAll1() {
        return this.tUserService.queryAllByLimit(0,2);
    }



}