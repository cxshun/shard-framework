package com.jdbc.shard.test;

import com.jdbc.shard.domain.Building;
import com.jdbc.shard.domain.Course;
import com.jdbc.shard.domain.Student;
import com.jdbc.shard.domain.Teacher;
import com.jdbc.shard.exception.DbException;
import com.jdbc.shard.shard.Shard;
import com.jdbc.shard.domain.Building;
import com.jdbc.shard.domain.Course;
import com.jdbc.shard.domain.Student;
import com.jdbc.shard.domain.Teacher;
import com.jdbc.shard.exception.DbException;
import com.jdbc.shard.shard.Shard;
import com.jdbc.shard.util.DbUtil;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by shun on 2015-12-23 17:01.
 */
public class TestDbUtil extends TestCase{

    public void test() throws DbException, SQLException {
        //hash
        Connection conn = DbUtil.getConnection(Teacher.class, new Shard(Teacher.class, 1));
        assertEquals(conn.getMetaData().getURL(), "jdbc:mysql://localhost:3306/data2");

        //range-hash
        conn = DbUtil.getConnection(Student.class, new Shard(Student.class, 4));
        assertEquals(conn.getMetaData().getURL(), "jdbc:mysql://localhost:3306/data1");

        //range
        conn = DbUtil.getConnection(Building.class, new Shard(Building.class, 70));
        assertEquals(conn.getMetaData().getURL(), "jdbc:mysql://localhost:3306/data4");

        conn = DbUtil.getConnection(Course.class);
        assertEquals(conn.getMetaData().getURL(), "jdbc:mysql://localhost:3306/data4");

        conn = DbUtil.getConnection(Course.class, null);
        assertEquals(conn.getMetaData().getURL(), "jdbc:mysql://localhost:3306/data4");
    }

}
