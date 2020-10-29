package com.example.plugins;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javafx.util.Pair;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/10/29 13:24
 */
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class}
)})
public class ResultPlugin  implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement = (Statement) invocation.getArgs()[0];
        ResultSetHandler resultSetHandler = this.getResultSetHandler(invocation);
        MetaObject metaObject = SystemMetaObject.forObject(resultSetHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
        //获取返回值类型
        Class<?> type = mappedStatement.getResultMaps().get(0).getType();
        //判断是否为ValueMap类型
        if(!ValueMap.class.isAssignableFrom(type)){
            return invocation.proceed();
        }
        Method currentMethod = findMethod(mappedStatement.getId());// 获取当前Method
        Pair<Class<?>, Class<?>> kvTypePair = getKVType(currentMethod);// 获取返回Map里key-value的类型
        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();// 获取各种TypeHander的注册器
        return convertToMap(statement, typeHandlerRegistry, kvTypePair);

    }

    private Pair<Class<?>, Class<?>> getKVType(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            if(parameterizedType.getActualTypeArguments().length!=2){
                throw new RuntimeException("使用ValueMap作为返回类型时必须指定K,V类型");
            }
            return new Pair<Class<?>, Class<?>>((Class<?>) parameterizedType.getActualTypeArguments()[0],
                    (Class<?>) parameterizedType.getActualTypeArguments()[1]);
        }
        return null;
    }


    private Method findMethod(String mappedStatementId) throws Throwable {
        String className = StringUtils.substringBeforeLast(mappedStatementId,".");
        String currentMethodName = StringUtils.substringAfterLast(mappedStatementId, ".");
        Method[] methods = Class.forName(className).getDeclaredMethods();// 该类所有声明的方法
        for (Method method : methods) {
            if (StringUtils.equals(method.getName(), currentMethodName)) {
                return method;
            }
        }
        return null;
    }
    private Object convertToMap(Statement statement, TypeHandlerRegistry typeHandlerRegistry, Pair<Class<?>, Class<?>> kvTypePair) throws Throwable {
        ResultSet resultSet = statement.getResultSet();
        List<Object> res = new ArrayList();
        ValueMap<Object, Object> map = new ValueMap();

        while (resultSet.next()) {
            Object key = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object value = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue());
            map.put(key, value);// 第一列作为key,第二列作为value。
        }

        res.add(map);
        return res;

    }

    private ResultSetHandler getResultSetHandler(Invocation invocation) throws Exception {
        ResultSetHandler target;
        Plugin plugin;
        Field field;
        for(target = (ResultSetHandler)invocation.getTarget(); Proxy.isProxyClass(target.getClass()); target = (ResultSetHandler)field.get(plugin)) {
            plugin = (Plugin)Proxy.getInvocationHandler(target);
            field = plugin.getClass().getDeclaredField("target");
            field.setAccessible(true);
        }

        return target;
    }

    private Object getObject(ResultSet resultSet, int columnIndex, TypeHandlerRegistry typeHandlerRegistry, Class<?> javaType) throws SQLException {
        final TypeHandler<?> typeHandler = typeHandlerRegistry.hasTypeHandler(javaType)
                ? typeHandlerRegistry.getTypeHandler(javaType) : typeHandlerRegistry.getUnknownTypeHandler();

        return typeHandler.getResult
                (resultSet, columnIndex);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
