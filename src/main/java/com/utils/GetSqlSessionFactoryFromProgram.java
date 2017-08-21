package com.utils;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class GetSqlSessionFactoryFromProgram {

    public static SqlSessionFactory getSqlSessionFactory() throws SQLException {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/yit_local_magento";
        String username = "root";
        String password = "yit123456";
        //创建使用缓存池的数据源  
        /*  
         * <dataSource type="POOLED"> 
                <property name="driver" value="${jdbc.driverClassName}"/> 
                <property name="url" value="${jdbc.url}"/> 
                <property name="username" value="${jdbc.username}"/> 
                <property name="password" value="${jdbc.password}"/> 
            </dataSource> 
         */
        DataSource dataSource = new PooledDataSource(driver, url, username, password);

        //创建事务  
        /* 
         * <transactionManager type="JDBC" /> 
         */
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        Environment environment = new Environment("development", transactionFactory, dataSource);

        Configuration configuration = new Configuration(environment);
        //加入资源  
        /* 
         * <mapper resource="ssm/BlogMapper.xml"/> 
         */

        //configuration.addMapper(UserMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        System.out.println(sqlSessionFactory);
        return sqlSessionFactory;
    }
}  