package com.keeper.model.dto;

import com.keeper.model.dao.GeoPoint;
import com.keeper.model.dao.Picture;
import com.keeper.model.dao.User;
import com.keeper.model.types.TaskState;
import com.keeper.model.types.TaskType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Alexandr Vasiliev on 29.03.2017.
 *
 * @author Alexandr Vasiliev
 *
 */
public class TaskDTO {
    public static final TaskDTO EMPTY = new TaskDTO();

    private Long id;
    private Long topicStarterId;
    private Long originGeoPointId;
    private TaskType type;
    private TaskState state = TaskState.HIDEN;
    private String theme;
    private String descr;
    private LocalDateTime createDate;
    private LocalDateTime lastModifyDate;

    private PictureDTO picture;
    private GeoPointDTO originGeoPoint;
    private List<CommentDTO> comments;
//    private List<UserDTO> participants;
//    private List<TagDTO> tags;

    private TaskDTO() {}

    public TaskDTO(Long topicStarterId, TaskType type, TaskState state, String theme, String descr) {
        this.topicStarterId = topicStarterId;
        this.type = type;
        this.state = state;
        this.theme = theme;
        this.descr = descr;
    }

    public TaskDTO(Long topicStarterId, Long originGeoPointId, TaskType type, TaskState state, String theme, String descr, PictureDTO picture) {
        this.topicStarterId = topicStarterId;
        this.originGeoPointId = originGeoPointId;
        this.type = type;
        this.state = state;
        this.theme = theme;
        this.descr = descr;
    }

    public TaskDTO(Long topicStarterId, Long originGeoPointId, TaskType type, TaskState state, String theme, String descr, PictureDTO picture,
                   Timestamp createDate, Timestamp lastModifyDate,
                   List<CommentDTO> comments, GeoPointDTO originGeoPoint/*List<UserDTO> participants, , List<TagDTO> tags*/) {
        this(topicStarterId, originGeoPointId, type, state, theme, descr ,picture);
        this.createDate = createDate.toLocalDateTime();
        this.lastModifyDate = lastModifyDate!=null ? lastModifyDate.toLocalDateTime() : createDate.toLocalDateTime();
        this.picture = picture;
        this.comments = comments;
        this.originGeoPoint = originGeoPoint;
//        this.participants = participants;
//        this.tags = tags;
    }
    //<editor-fold desc="GetterAndSetter">

    public Long getId() {
        return id;
    }

    public Long getTopicStarterId() {
        return topicStarterId;
    }

    public Long getOriginGeoPointId() {
        return originGeoPointId;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(LocalDateTime lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public TaskType getType() {
        return type;
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

    public PictureDTO getPicture() {
        return picture;
    }

    public void setPicture(PictureDTO picture) {
        this.picture = picture;
    }

    public GeoPointDTO getOriginGeoPoint() {
        return originGeoPoint;
    }

    public void setOriginGeoPoint(GeoPointDTO geoPoint) {
        this.originGeoPoint = originGeoPoint;
    }

    public void setTopicStarterId(Long topicStarterId) {
        this.topicStarterId = topicStarterId;
    }

    public void setOriginGeoPointId(Long originGeoPointId) {
        this.originGeoPointId = originGeoPointId;
    }

    //    public List<UserDTO> getParticipants() {
//        return participants;
//    }
//
//    public void setParticipants(List<UserDTO> participants) {
//        this.participants = participants;
//    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

//    public List<TagDTO> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<TagDTO> tags) {
//        this.tags = tags;
//    }

    //</editor-fold>
}
