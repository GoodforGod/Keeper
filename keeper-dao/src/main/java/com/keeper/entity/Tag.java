package com.keeper.entity;

/*
 * Created by @GoodforGod on 4.04.2017.
 */

import com.keeper.util.DatabaseResolver;

import javax.persistence.*;
import javax.xml.crypto.Data;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Default Comment
 */
@Entity
@Table(name = DatabaseResolver.TABLE_TAGS, schema = DatabaseResolver.SCHEMA)
public class Tag {

    public static final Tag empty = new Tag();

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)   private Long taskId;
    @Column(name = "value")                                 private String value;

    private Tag() { }

    public Tag(Long taskId, String value) {
        this.taskId = taskId;
        this.value = value;
    }

    //<editor-fold desc="GetterAndSetter">

    public Long getTaskId() {
        return taskId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return value != null ? value.equals(tag.value) : tag.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}