package com.jdbc.shard.shard;

/**
 * Created by shun on 2015-12-16 18:06.
 */
public class Shard {

    private Class clazz;
    private int value;

    public Shard() {}
    public Shard(Class clazz, int value) {
        this.clazz = clazz;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

}
