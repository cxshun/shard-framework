package com.shard.spring.support;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by shun on 16/2/8.
 */
public class ShardJdbcTemplate extends JdbcTemplate{

    public ShardJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }
}
