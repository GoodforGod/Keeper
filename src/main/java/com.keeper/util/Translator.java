package com.keeper.util;

/*
 * Created by @GoodforGod on 7.04.2017.
 */

import com.keeper.model.SimpleGeoPoint;
import com.keeper.model.dao.*;
import com.keeper.model.dto.*;
import com.keeper.model.types.UserType;
import com.keeper.test.model.dao.UserTest;
import com.keeper.test.model.dao.ZoneTest;
import com.keeper.test.model.dto.UserTestDTO;
import com.keeper.test.model.dto.ZoneTestDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Translate and full fill DTO object to DAO object and revert
 */
public class Translator {

    //<editor-fold desc="toDTO">

    public static TaskDTO toDTO(Task model) {
        return (model == null)
                ? TaskDTO.EMPTY
                : new TaskDTO(
                            model.getTopicStarterId(),
                            model.getOriginGeoPointId(),
                            model.getType(),
                            model.getState(),
                            model.getTheme(),
                            model.getDescr(),
                            Translator.toDTO(model.getPicture()),
                            model.getCreateDate(),
                            model.getLastModifyDate(),
                            commentsToDTO(model.getComments()),
                            toDTO(model.getOriginGeoPoint()),
                            usersToDTO(model.getParticipants()),
                            tagsToDTO(model.getTags())
//                            toDTO(model.getPicture())
        ) {{ setId(model.getId()); }};
    }

    public static GeoPointDTO toDTO(GeoPoint model) {
        return (model == null)
                ? GeoPointDTO.EMPTY
                : new GeoPointDTO(model.getId(),
                                model.getLatitude().toString(),
                                model.getLongitude().toString(),
                                model.getRadius(),
                                model.getInfo());
    }

    public static UserDTO toDTO(User model) {
        return (model == null)
                ? UserDTO.EMPTY
                : new UserDTO(model.getId(),
                            model.getType(),
                            model.getState(),
                            model.getName(),
                            model.getEmail(),
                            model.getPhone(),
                            model.getAbout(),
                            model.getPassword(),
                            (model.getNotified() == null) ? false : model.getNotified(),
                            (model.getMuteStart() != null)
                                    ? model.getMuteStart().toLocalDateTime()
                                    : LocalDateTime.MIN,
                            (model.getMuteEnd() != null)
                                    ? model.getMuteEnd().toLocalDateTime()
                                    : LocalDateTime.MIN,
                            Translator.toDTO(model.getPic()),
                            toDTO(model.getZone()),
                            geoPointsToDTO(model.getGeoPoints()),
                            routesToDTO(model.getRoutes())/*,
                            tasksToDTO(model.getParticipantedTasks())*/
        );
    }

    public static ZoneDTO toDTO(Zone model) {
        return (model == null)
                ? ZoneDTO.EMPTY
                : new ZoneDTO(model.getProfileId(),
                            model.getCity(),
                            model.getCountry(),
                            model.getRegisterDate());
    }

    public static PictureDTO toDTO(Picture model) {
        return (model == null)
                ? PictureDTO.EMPTY
                : new PictureDTO(model.getUserId(),
                                model.getTaskId(),
                                model.getPic(),
                                model.getInfo());
    }

    public static CommentDTO toDTO(Comment model) {
        return (model == null)
                ? CommentDTO.EMPTY
                : new CommentDTO(model.getId(),
                                model.getTaskId(),
                                model.getUserId(),
                                model.getCreateDate(),
                                model.getLastModifyDate(),
                                model.getMessage(),
                                new SimpleGeoPoint(model.getLongtitude(), model.getLatitude())
        );
    }

    public static RouteDTO toDTO(Route model) {
        return (model == null)
                ? RouteDTO.EMPTY
                : new RouteDTO(model.getId(),
                                model.getUserId(),
                                model.getType(),
                                model.getInfo(),
                                model.getRadius(),
                                model.getLatitudes(),
                                model.getLongtitudes());
    }

    public static TagDTO toDTO(Tag model) {
        return (model == null)
                ? TagDTO.EMPTY
                : new TagDTO(model.getId(),
                            model.getTag(),
                            model.getCounter()
        );
    }

    //<editor-fold desc="Lists">

    public static List<PictureDTO> picsToDTO(List<Picture> models) {
        return models.stream().map(Translator::toDTO).collect(Collectors.toList());
    }

    public static List<CommentDTO> commentsToDTO(List<Comment> models) {
        return models.stream().map(Translator::toDTO).collect(Collectors.toList());
    }

    public static List<TagDTO> tagsToDTO(List<Tag> models) {
        return models.stream().map(Translator::toDTO).collect(Collectors.toList());
    }

    public static List<UserDTO> usersToDTO(List<User> models) {
        return models.stream().map(Translator::toDTO).collect(Collectors.toList());
    }

    public static List<TaskDTO> tasksToDTO(List<Task> models) {
        return models.stream().map(Translator::toDTO).collect(Collectors.toList());
    }

    public static List<GeoPointDTO> geoPointsToDTO(List<GeoPoint> models) {
        return models.stream().map(Translator::toDTO).collect(Collectors.toList());
    }

    public static List<RouteDTO> routesToDTO(List<Route> models) {
        return models.stream().map(Translator::toDTO).collect(Collectors.toList());
    }

    public static List<Comment> commentsToDAO(List<CommentDTO> models) {
        return models.stream().map(Translator::toDAO).collect(Collectors.toList());
    }

    public static List<SimpleGeoPoint> toSimpleGeoPoints(String[] latitude, String[] longtitude) {
        if(latitude == null || longtitude == null || latitude.length != longtitude.length)
            return null;

        List<SimpleGeoPoint> geoPoints = new ArrayList<>();

        for(int i = 0; i < latitude.length; i ++)
            geoPoints.add(new SimpleGeoPoint(latitude[i], longtitude[i]));

        return geoPoints;
    }

    //</editor-fold>

    //<editor-fold desc="Testing">

    public static UserTestDTO toDTO(UserTest model) {
        return (model == null)
                ? UserTestDTO.EMPTY
                : new UserTestDTO(model.getId(),
                model.getType(),
                model.getState(),
                model.getName(),
                model.getMaskedEmail(),
                model.getPhone(),
                model.getAbout(),
                model.getNotified(),
                model.getMuteStart().toLocalDateTime(),
                model.getMuteEnd().toLocalDateTime());
    }

    public static ZoneTestDTO toDTO(ZoneTest model) {
        return (model == null)
                ? ZoneTestDTO.EMPTY
                : new ZoneTestDTO(model.getUserId(),
                model.getCity(),
                model.getCountry(),
                model.getRegisterDate());
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="toDAO">

    public static User toDAO(UserDTO model) {
        return (model == null)
                ? User.EMPTY
                : new User(model.getType(),
                            model.getName(),
                            model.getEmail(),
                            model.getPhone(),
                            model.getPassword(),
                            model.getAbout(),
                            model.getNotified(),
                            model.getMuteStart(),
                            model.getMuteEnd());
    }

    public static Tag toDAO(TagDTO model) {
        return (model == null)
                ? Tag.EMPTY
                : new Tag(model.getId(),
                        model.getTag(),
                        model.getCounter());
    }

    public static User toDAO(UserFormDTO model) {
        return (model == null)
                ? User.EMPTY
                : new User(UserType.USER,
                            model.getName(),
                            model.getEmail(),
                            "",
                            model.getPassword(),
                            "");
    }


    public static Task toDAO(TaskDTO model) {
        return (model == null)
                ? Task.EMPTY
                : new Task(
//                            model.getCreateDate(),
//                            model.getLastModifyDate(),
                            model.getTopicStarterId(),
                            model.getOriginGeoPointId(),
                            model.getType(),
                            model.getState(),
                            model.getTheme(),
                            model.getDescr()/*,
                            Translator.toDAO(model.getPicture()),
                            Translator.commentsToDAO(model.getComments()),
                            Translator.toDAO(model.getOriginGeoPoint())*/
                );
    }

    public static GeoPoint toDAO(GeoPointDTO model) {
        return (model == null)
                ? GeoPoint.EMPTY
                : new GeoPoint(model.getLatitude(),
                                model.getLongitude(),
                                model.getRadius(),
                                model.getInfo());
    }

    public static Route toDAO(RouteDTO model) {
        return (model == null)
                ? Route.EMPTY
                : new Route(model.getUserId(),
                            model.getType(),
                            model.getInfo(),
                            model.getRadius(),
                            model.getPoints());
    }

    public static Zone toDAO(ZoneDTO model) {
        return (model == null)
                ? Zone.EMPTY
                : new Zone(
                        model.getprofileId(),
                        model.getCity(),
                        model.getCountry()
//                        ,model.getRegisterDate().toLocalDateTime()
        );
    }

    public static Picture toDAO(PictureDTO model) {
        return (model == null)
                ? Picture.EMPTY
                : new Picture(
                model.getUserId(),
                model.getTaskId(),
                model.getPic(),
                model.getInfo()
        );
    }

    public static Comment toDAO(CommentDTO model) {
        return (model == null)
                ? Comment.EMPTY
                : new Comment(
                model.getTaskId(),
                model.getUserId(),
//                model.getCreateDate(),
//                model.getLastModifyDate(),
                model.getMessage(),
                new SimpleGeoPoint(model.getLongtitude(), model.getLatitude())
        );
    }
    //</editor-fold>
}
