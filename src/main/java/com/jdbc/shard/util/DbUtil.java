package com.jdbc.shard.util;

import com.jdbc.shard.database.DbInfo;
import com.jdbc.shard.exception.DbException;
import com.jdbc.shard.exception.NoMatchDataSourceException;
import com.jdbc.shard.reader.impl.ShardXmlReader;
import com.jdbc.shard.shard.Shard;
import com.jdbc.shard.shard.ShardProperty;
import com.jdbc.shard.shard.ShardType;
import com.jdbc.shard.reader.impl.DbXmlReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shun on 2015-12-17 14:41.
 */
public class DbUtil {

    private static Map<String, DbInfo> dbInfoMap = new HashMap<String, DbInfo>();
    private static Map<String, Connection> connectionMap = new HashMap<String, Connection>();

    private static List<ShardProperty> shardProperties = new ArrayList<ShardProperty>();
    static {
        try {
            List<DbInfo> dbInfoList = new DbXmlReader().process(DbUtil.class.getClass().getResource("/database.xml").getPath(), DbInfo.class);
            for (DbInfo dbInfo:dbInfoList) {
                dbInfoMap.put(dbInfo.getId(), dbInfo);
            }

            shardProperties = new ShardXmlReader().process(DbUtil.class.getResource("/shard.xml").getPath(), ShardProperty.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get connection for the specify dataSource
     * @param dataSourceId
     * @return
     * @throws com.jdbc.shard.exception.NoMatchDataSourceException
     */
    private static Connection getConnection(String dataSourceId) throws NoMatchDataSourceException {
        DbInfo dbInfo = dbInfoMap.get(dataSourceId);

        if (dbInfo == null) {
            throw new NoMatchDataSourceException("no match dataSource found for dataSourceId:%s", dataSourceId);
        }

        if (connectionMap.get(dataSourceId) != null) {
            return connectionMap.get(dataSourceId);
        }

        try {
            Class.forName(dbInfo.getDriverClass());
            Connection conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());
            //cached for the future use
            connectionMap.put(dbInfo.getId(), conn);
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new NoMatchDataSourceException("no match dataSource found for dataSourceId:%s", dataSourceId);
    }


    //mark used for split range value
    private static final String RANGE_SPLITTER = "~";
    //mark used for splitting range and hash-val for the range-hash type
    private static final String RANGE_HASH_SPLITTER = "/";
    //map used to saved the range-hash match list
    private static final Map<String, List<ShardProperty.MatchInfo>> rangeHashMap = new HashMap<String, List<ShardProperty.MatchInfo>>();

    /**
     * get connection by shard configuration for the provided class
     * @param clazz
     * @param shard         shard property
     * @return
     * @throws com.jdbc.shard.exception.DbException
     */
    public static Connection getConnection(Class clazz, Shard shard) throws DbException {
        //no shard provided
        if (shard == null) {
            return getConnection(clazz);
        }

        for (ShardProperty shardProperty:shardProperties) {
            if (shardProperty.getClazz().equals(clazz.getName())) {
                List<ShardProperty.MatchInfo> matchInfoList = shardProperty.getMatchInfoList();
                if (shardProperty.getType().equals(ShardType.HASH)) {
                    //calculate hash value
                    int hashVal = shard.getValue() % matchInfoList.size();
                    for (ShardProperty.MatchInfo matchInfo:matchInfoList) {
                        if (Integer.valueOf(matchInfo.getMatch()) == hashVal) {
                            return getConnection(matchInfo.getDataSourceId());
                        }
                    }
                } else if (shardProperty.getType().equals(ShardType.RANGE)) {
                    for (ShardProperty.MatchInfo matchInfo:matchInfoList) {
                        //get range value
                        int low = Integer.parseInt(matchInfo.getMatch().split(RANGE_SPLITTER)[0]);
                        int high = Integer.parseInt(matchInfo.getMatch().split(RANGE_SPLITTER)[1]);

                        //check if in the current scope
                        if (shard.getValue() >= low && shard.getValue() <= high) {
                            return getConnection(matchInfo.getDataSourceId());
                        }
                    }
                } else if (shardProperty.getType().equals(ShardType.RANGE_HASH)) {
                    for (ShardProperty.MatchInfo matchInfo:matchInfoList) {
                        //get range value
                        int low = Integer.parseInt(matchInfo.getMatch().split(RANGE_HASH_SPLITTER)[0].split(RANGE_SPLITTER)[0]);
                        int high = Integer.parseInt(matchInfo.getMatch().split(RANGE_HASH_SPLITTER)[0].split(RANGE_SPLITTER)[1]);

                        //check if in the current scope
                        if (shard.getValue() >= low && shard.getValue() <= high) {
                            List<ShardProperty.MatchInfo> miList = getRangeMatchInfos(matchInfoList, matchInfo.getMatch().split(RANGE_HASH_SPLITTER)[0]);

                            int hashVal = shard.getValue() % miList.size();
                            for (ShardProperty.MatchInfo mi:miList) {
                                if (hashVal == Integer.valueOf(mi.getMatch().split(RANGE_HASH_SPLITTER)[1])) {
                                    return getConnection(mi.getDataSourceId());
                                }
                            }
                        }
                    }
                } else if (shardProperty.getType().equals(ShardType.NONE)) {
                    return getConnection(shardProperty.getMatchInfoList().get(0).getDataSourceId());
                }
            }
        }
        throw new NoMatchDataSourceException("no match shard dataSource");
    }

    /**
     * get the specify rangeVal's range-hash match list
     * @param matchInfoList     all the matches in the current shard node
     * @param rangeVal          range value that need to find the match list
     * @return
     */
    private static List<ShardProperty.MatchInfo> getRangeMatchInfos(List<ShardProperty.MatchInfo> matchInfoList, String rangeVal) {
        //if existed, then get from memory
        if (rangeHashMap.containsKey(rangeVal)) {
            return rangeHashMap.get(rangeVal);
        }

        List<ShardProperty.MatchInfo> miList = new ArrayList<ShardProperty.MatchInfo>();
        for (ShardProperty.MatchInfo matchInfo:matchInfoList) {
            if (matchInfo.getMatch().split(RANGE_HASH_SPLITTER)[0].equals(rangeVal)) {
                miList.add(matchInfo);
            }
        }

        //cached it
        rangeHashMap.put(rangeVal, miList);
        return miList;
    }

    /**
     * getConnection for the not shard specify class
     * @param clazz
     * @return
     * @throws com.jdbc.shard.exception.NoMatchDataSourceException
     */
    public static Connection getConnection(Class clazz) throws NoMatchDataSourceException {
        for (ShardProperty shardProperty:shardProperties) {
            if (shardProperty.getClazz().equals(clazz.getName()) && shardProperty.getType().equals(ShardType.NONE)) {
                return getConnection(shardProperty.getMatchInfoList().get(0).getDataSourceId());
            }
        }
        throw new NoMatchDataSourceException("no match shard datasource");
    }



}
