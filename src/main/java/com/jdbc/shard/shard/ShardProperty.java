package com.jdbc.shard.shard;

import java.util.List;

/**
 * Created by shun on 2015-12-16 16:00.
 */
public class ShardProperty {

    /**
     * shard class name, all specify name, including package name
     */
    private String clazz;
    /**
     * shard type
     */
    private String type;
    /**
     * shard match properties
     */
    private List<MatchInfo> matchInfoList;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MatchInfo> getMatchInfoList() {
        return matchInfoList;
    }

    public void setMatchInfoList(List<MatchInfo> matchInfoList) {
        this.matchInfoList = matchInfoList;
    }

    /**
     * shard matches
     */
    public static final class MatchInfo {
        private String match;
        private String dataSourceId;

        public MatchInfo(String match, String dataSourceId) {
            this.match = match;
            this.dataSourceId = dataSourceId;
        }

        public String getMatch() {
            return match;
        }

        public String getDataSourceId() {
            return dataSourceId;
        }
    }
}
