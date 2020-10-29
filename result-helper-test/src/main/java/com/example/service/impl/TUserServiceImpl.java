package com.example.service.impl;

import com.example.dao.TUserDao;
import com.example.entity.TUser;
import com.example.plugins.ValueMap;
import com.example.service.TUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * (TUser)表服务实现类
 *
 * @author makejava
 * @since 2020-09-28 18:22:56
 */
@Service("tUserService")
public class TUserServiceImpl implements TUserService {
    @Resource
    private TUserDao tUserDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Map queryById(Integer id) {
        return this.tUserDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<TUser> queryAllByLimit(int offset, int limit) {
        return this.tUserDao.queryAllByLimit(offset, limit);
    }

    @Override
    public ValueMap<Integer, String> queryAll() {
        ValueMap<Integer, String> map = tUserDao.queryAll(new TUser());

        return map;
    }

}