package com.example.dao;

import java.util.List;
import java.util.Map;

import com.example.plugins.ValueMap;
import org.apache.ibatis.annotations.Param;

import com.example.entity.TUser;

/**
 * (TUser)表数据库访问层
 *
 * @author makejava
 * @since 2020-09-28 18:22:52
 */
public interface TUserDao {


    Map queryById(Integer id);



    List<TUser> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param tUser 实例对象
     * @return 对象列表
     */
    ValueMap<Integer,String> queryAll(TUser tUser);


}