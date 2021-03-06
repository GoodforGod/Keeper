package com.keeper.model.dao;

/*
 * Created by GoodforGod on 19.03.2017.
 *
 * Updated by AlexVasil on 30.03.2017.
 *
 */

import com.keeper.model.types.TaskState;
import com.keeper.model.types.TaskType;
import com.keeper.model.types.UserType;
import com.keeper.model.util.SimpleGeoPoint;
import com.keeper.util.resolvers.DatabaseResolver;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Task model implementation
 */
@Entity
@Table(name = DatabaseResolver.TABLE_TASKS, schema = DatabaseResolver.SCHEMA)
public class Task {

    public static final Task EMPTY = new Task();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "topicStarterId", nullable = false)
    private Long topicStarterId;

    @Column(name = "type", nullable = false)
    private TaskType type;

    @Column(name = "state")
    private TaskState state = TaskState.OKAY;

    @Column(name = "theme", nullable = false)
    private String theme;

    @Column(name = "descr")
    private String descr;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "radius")
    private Integer radius;

    @Column(name = "createDate", nullable = false)
    private Timestamp createDate;

    @Column(name = "lastModifyDate", nullable = false)
    private Timestamp lastModifyDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id", referencedColumnName = "taskId")
    private Picture picture;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "taskId", referencedColumnName = "id")
    private List<Comment> comments;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = DatabaseResolver.TABLE_TAG_MANAGER, schema = DatabaseResolver.SCHEMA,
            joinColumns = @JoinColumn(name = "taskId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tagId", referencedColumnName = "id"))
    private Set<Tag> tags = new HashSet<>();

    private Task() {
        this.id = TaskType.EMPTY.getValue();
        this.topicStarterId = UserType.EMPTY.getValue();
        this.createDate = Timestamp.valueOf(LocalDateTime.MIN);
        this.lastModifyDate = Timestamp.valueOf(LocalDateTime.MAX);
        this.type = TaskType.EMPTY;
        this.state = TaskState.UNKNOWN;
        this.theme = "";
        this.descr = "";
    }

    public Task(Long topicStarterId, TaskType type, String theme, String descr) {
        this.state = TaskState.OKAY;
        this.topicStarterId = topicStarterId;
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
        this.lastModifyDate = Timestamp.valueOf(LocalDateTime.now());
        this.type = type;
        this.theme = theme;
        this.descr = descr;

    }

    public Task(Long topicStarterId, TaskType type, String theme, String descr, SimpleGeoPoint geo) {
        this(topicStarterId, type, theme, descr);
        this.latitude = (geo != null) ? geo.getLatitude() : null;
        this.longitude = (geo != null) ? geo.getLongitude() : null;
        this.radius = (geo != null) ? geo.getRadius() : null;
    }

    public Task(Long topicStarterId, TaskType type, String theme, String descr,
                Picture picture, List<Comment> comments, SimpleGeoPoint geo,
                Set<Tag> tags) {
        this(topicStarterId, type, theme, descr);
        this.picture = picture;
        this.comments = comments;
        this.latitude = geo.getLatitude();
        this.longitude = geo.getLongitude();
        this.radius = geo.getRadius();
        this.tags = tags;
    }

    public void setGeo(SimpleGeoPoint geo) {
        this.latitude = geo.getLatitude();
        this.longitude = geo.getLongitude();
        this.radius = geo.getRadius();
    }

    public SimpleGeoPoint getGeo() {
        return new SimpleGeoPoint(latitude.toString(), longitude.toString(), radius);
    }

    //<editor-fold desc="GetterAndSetter">

    public Picture getPicture() {
        return picture;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Long getId() {
        return id;
    }

    public Long getTopicStarterId() {
        return topicStarterId;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Timestamp lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    //</editor-fold>

    private boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    public void addTag(Tag tag) {
        if (!hasTag(tag)) {
            tag.incCounter();
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        if (hasTag(tag)) {
            tags.remove(tag);
            tag.decCounter();
        }
    }

    @Override
    public String toString() {
        return "Task{ id=" + id
                + ", createDate=" + createDate
                + ", lastModifyDate=" + lastModifyDate
                + ", topicStarterId=" + topicStarterId
                + ", descr='" + descr + '\''
                + ", picture=" + picture
                + '}';
    }
}
