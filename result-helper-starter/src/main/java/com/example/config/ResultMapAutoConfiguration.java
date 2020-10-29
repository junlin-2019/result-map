package com.example.config;

import com.example.plugins.ResultPlugin;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/10/29 16:58
 */
@ConditionalOnBean(SqlSessionFactory.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class ResultMapAutoConfiguration {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addPageInterceptor() {
        ResultPlugin interceptor = new ResultPlugin();
        Iterator iterator = this.sqlSessionFactoryList.iterator();

        while(iterator.hasNext()) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)iterator.next();
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            configuration.addInterceptor(interceptor);
        }
    }

}

