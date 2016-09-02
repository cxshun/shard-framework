package com.shard.jdbc.shard;

import java.io.Serializable;

/**
 * Shard configuration props
 * Created by shun on 2015-12-16 18:06.
 */
public class Shard implements Serializable{

    private static final long serialVersionUID = -2405737710724475171L;
    private Class clazz;
    private String propName;
    private int value;

    public Shard() {}
    public Shard(Class clazz, String propName, int value) {
        this.clazz = clazz;
        this.propName = propName;
        this.value = value;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
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
