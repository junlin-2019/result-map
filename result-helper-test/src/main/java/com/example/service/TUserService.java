package com.example.service;

import com.example.entity.TUser;
import com.example.plugins.ValueMap;

import java.util.List;
import java.util.Map;

/**
 * (TUser)表服务接口
 *
 * @author makejava
 * @since 2020-09-28 18:22:55
 */
public interface TUserService {

    Map queryById(Integer id);

    List<TUser> queryAllByLimit(int offset, int limit);


    ValueMap<Integer, String> queryAll();



}