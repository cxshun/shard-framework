package com.shard.spring.support;

import com.shard.jdbc.domain.Teacher;
import com.shard.jdbc.exception.DbException;
import com.shard.jdbc.shard.Shard;
import com.shard.jdbc.util.DbUtil;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by shun on 16/2/8.
 */
public class ShardJdbcTemplateTest extends TestCase{

    @Test
    public void test() throws DbException, SQLException {
        DataSource dataSource = DbUtil.getDataSource(Teacher.class, new Shard(Teacher.class, 1));

        ShardJdbcTemplate shardJdbcTemplate = new ShardJdbcTemplate(dataSource);
        List<Teacher> teacherList = shardJdbcTemplate.query("select * from teacher", new BeanPropertyRowMapper(Teacher.class));
        System.out.println(teacherList);
    }

}
