package com.shard.spring.support;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by shun on 16/2/8.
 */
public class ShardJdbcTemplate extends JdbcTemplate{

    private Logger logger = Logger.getLogger(ShardJdbcTemplate.class.getName());

    public ShardJdbcTemplate(DataSource dataSource) {
        super(dataSource);
        try {
            logger.log(Level.INFO, String.format("connected to database:%s", dataSource.getConnection().getMetaData().getURL()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
