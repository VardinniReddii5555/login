package com.emudhra.Registration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false , length = 50)
    private String username;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false,length = 225)
    private String password;

    @Column(nullable = false, length = 30)
    private String registration_mode;

    @Column(name = "push_registered")
    private Boolean pushRegistered = false;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    // Getters & Setters


    public Users() {}

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", registration_mode='" + registration_mode + '\'' +
                ", pushRegistered=" + pushRegistered +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistration_mode() {
        return registration_mode;
    }

    public void setRegistration_mode(String registration_mode) {
        this.registration_mode = registration_mode;
    }

    public Boolean getPushRegistered() {
        return pushRegistered;
    }

    public void setPushRegistered(Boolean pushRegistered) {
        this.pushRegistered = pushRegistered;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
