package com.keeper.entity.dao;

/**
 * Created by Alexandr Vasiliev on 04.04.2017.
 *
 * @author Alexandr Vasiliev
 */

import com.keeper.entity.ModelManager;
import com.keeper.entity.states.UserState;
import com.keeper.entity.states.UserType;
import com.keeper.util.Validator;
import com.keeper.util.dao.DatabaseResolver;
import com.sun.istack.internal.Nullable;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;

import javax.persistence.*;
import java.sql.Timestamp;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = DatabaseResolver.TEST_TABLE_USERS, schema = DatabaseResolver.TEST_SCHEMA)
public class UserTest {

    public static final UserTest empty = new UserTest();

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)   private Long id;
    @Column(name = "state")                                 private UserState state;
    @Column(name = "type")                                  private UserType type;
    @Column(name = "name", nullable = false)                private String name;
    @Column(name = "email", nullable = false)               private String email;
    @Column(name = "phone")                                 private String phone;
    @Column(name = "password", nullable = false)            private String password;
    @Column(name = "about")                                 private String about;
    @Column(name = "isNotified")                            private Boolean isNotified;
    @Column(name = "startMuteTime")                         private Timestamp muteStart;
    @Column(name = "endMuteTime")                           private Timestamp muteEnd;

    private UserTest() { }

    public UserTest(UserType type,
                    String name,
                    String email,
                    String phone,
                    String password,
                    String about) throws NullAttributeException {

        if(email != null && !email.isEmpty())
            throw new NullAttributeException("Nullable param", "EMAIL");
        if(password != null && !password.isEmpty())
            throw new NullAttributeException("Nullable param", "PASSWORD");
        if(name != null && !name.isEmpty())
            throw new NullAttributeException("Nullable param", "NAME");

        this.state      = UserState.AWAIT_VERIFICATION;
        this.type       = type != null ? type : UserType.USER;
        this.name       = name;
        this.email      = Validator.generateHashcode(email, Validator.HashType.EMAIL);
        this.phone      = phone;
        this.password   = Validator.generateHashcode(password, Validator.HashType.PASS);
        this.about      = about;
        this.isNotified = false;
    }

    @Nullable
    public static UserTest gen(UserType type,
                               String name,
                               String email,
                               String phone,
                               String password,
                               String about) {
        try {
            return new UserTest(type, name, email, phone, password, about);
        } catch (NullAttributeException e) {
            ModelManager.logConstactionError("GEN", e);
        }

        return null;
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
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserTest userTest = (UserTest) o;

        return email.equals(userTest.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}