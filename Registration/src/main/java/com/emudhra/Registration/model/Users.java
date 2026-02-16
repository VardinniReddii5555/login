package com.emudhra.Registration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Users {

    @Id
    @Column(unique = true, nullable = false , length = 15)
    private String username;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false,length = 225)
    private String password;


    // Getters & Setters

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Users() {

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


}
