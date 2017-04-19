package com.keeper.model.dao;

/*
 * Created by @GoodforGod on 6.04.2017.
 */

import com.keeper.model.ModelLoggerManager;
import com.keeper.model.types.UserState;
import com.keeper.model.types.UserType;
import com.keeper.util.Hasher;
import com.keeper.util.dao.DatabaseResolver;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default Comment
 */
@Entity
@Table(name = DatabaseResolver.TABLE_USERS, schema = DatabaseResolver.SCHEMA)
public class User {

    public static final User EMPTY = new User((long)UserType.EMPTY.getValue(), UserType.EMPTY);

    @Id
    @Column(name = "id", unique = true, nullable = false)   private Long id;
    @Column(name = "state")                                     private UserState state;
    @Column(name = "type")                                      private UserType type;
    @Column(name = "name",       nullable = false)              private String name;
    @Column(name = "email",      nullable = false)              private String email;
    @Column(name = "phone")                                     private String phone;
    @Column(name = "password",   nullable = false)              private String password;
    @Column(name = "about")                                     private String about;
    @Column(name = "isNotified")                                private Boolean isNotified;
    @Column(name = "startMuteTime")                             private Timestamp muteStart;
    @Column(name = "endMuteTime")                               private Timestamp muteEnd;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn(name = "id", referencedColumnName = "userId")
    private Zone zone;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn(name = "id", referencedColumnName = "userId")
    private Picture pic;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="userId", referencedColumnName="id")
    private List<Route> routes;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userId", referencedColumnName="id")
    private List<Comment> comments;

    // THIS TYPE OF SELECT VIA DIRECT JOIN DOES NOT WORK
//    @Fetch(FetchMode.SELECT)
//    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = DatabaseResolver.TABLE_GEOMANAGER,
               joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "geopointId", referencedColumnName = "id"))
    private List<GeoPoint> geoPoints;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = DatabaseResolver.TABLE_PARTICINATMAANGER,
               joinColumns = @JoinColumn(name = "userId", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name = "taskId", referencedColumnName="id"))
    private List<Task> participantedTasks;

    private User() {
        this.id         = (long) UserType.UNKNOWN.getValue();
        this.state      = UserState.UNKNOWN;
        this.type       = UserType.UNKNOWN;
        this.name       = "";
        this.email      = "";
        this.phone      = "";
        this.password   = "";
        this.about      = "";
        this.isNotified = false;
        this.muteStart  = Timestamp.valueOf(LocalDateTime.MIN);
        this.muteEnd    = Timestamp.valueOf(LocalDateTime.MAX);
    }

    private User(Long id, UserType type) {
        super();
        this.id = id;
        this.type = type;
    }

    public User(UserType type, String name,
                String email, String phone,
                String password, String about) {
        this.id         = Hasher.generateHashSimple(email, Hasher.HashType.EMAIL);
        this.state      = UserState.AWAIT_VERIFICATION;
        this.type       = type != null ? type : UserType.UNKNOWN;
        this.name       = name;
        this.email      = email; //Hasher.generateHashCrypto(email, Hasher.HashType.EMAIL);
        this.phone      = phone;
        this.password   = Hasher.generateHashCrypto(password, Hasher.HashType.PASS);
        this.about      = about;
        this.isNotified = false;
    }

    public User(UserType type, String name,
                String email, String phone,
                String password, String about, boolean isNotified,
                LocalDateTime muteStart, LocalDateTime muteEnd)
    {
        this(type, name, email, phone, password, about);
        this.isNotified = isNotified;
        this.muteStart  = Timestamp.valueOf(muteStart);
        this.muteEnd    = Timestamp.valueOf(muteEnd);
    }

    //<editor-fold desc="GetterAndSetter">

    public Long getId() {
        return id;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Boolean getNotified() {
        return isNotified;
    }

    public void setNotified(Boolean notified) {
        isNotified = notified;
    }

    public Timestamp getMuteStart() {
        return muteStart;
    }

    public void setMuteStart(Timestamp muteStart) {
        this.muteStart = muteStart;
    }

    public Timestamp getMuteEnd() {
        return muteEnd;
    }

    public void setMuteEnd(Timestamp muteEnd) {
        this.muteEnd = muteEnd;
    }

    public Picture getPic() {
        return pic;
    }

    public void setPic(Picture pic) {
        this.pic = pic;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public List<Route> getRoutes() {
        return (routes == null) ? Collections.emptyList() : routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Comment> getComments() {
        return (comments == null) ? Collections.emptyList() : comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }

    public List<Task> getParticipantedTasks() {
        return participantedTasks;
    }

    public void setParticipantedTasks(List<Task> participantedTasks) {
        this.participantedTasks = participantedTasks;
    }

    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
