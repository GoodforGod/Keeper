package com.keeper.entity;

/*
 * Created by @GoodforGod on 4.04.2017.
 */

import com.keeper.util.DatabaseResolver;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Default Comment
 */
@Entity
@Table(name = DatabaseResolver.TABLE_GEOMANAGER, schema = DatabaseResolver.SCHEMA)
public class GeoManager {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)   private Long id;
    @Column(name = "userId")                                private Long userId;
    @Column(name = "taskId")                                private Long taskId;
    @Column(name = "routeId")                               private Long routeId;
    @Column(name = "pointId")                               private Long pointId;

    //<editor-fold desc="GetterAndSetter">

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }
    //</editor-fold>
}