package com.shard.spring.support;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by shun on 16/2/8.
 */
public class ShardJdbcTemplate extends JdbcTemplate{

    private Logger logger = Logger.getRootLogger();

    public ShardJdbcTemplate(DataSource dataSource) {
        super(dataSource);
        try {
            logger.info(String.format("connected to database:%s", dataSource.getConnection().getMetaData().getURL()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
