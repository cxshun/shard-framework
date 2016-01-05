package com.jdbc.shard.exception;

/**
 * Created by roy@warthog.cn on 2015-12-24 15:15.
 */
public class InvalidShardConfException extends DbException{
    public InvalidShardConfException(String msg, Object... args) {
        super(msg, args);
    }
}
