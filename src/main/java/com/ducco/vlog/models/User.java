package com.ducco.vlog.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    private boolean enabled;
    private boolean tokenNonExpired;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="role_id",referencedColumnName = "id")
    )
    private Collection<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Post> posts;


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for MySQL
//    private Long id;
//
//    private String firstName;
//    private String lastName;
//
//    @Column(unique = true, nullable = false) // Ensure email is unique
//    private String email;
//
//    private String password;
//    private boolean enabled;
//    private boolean tokenExpired;
//
//    @ManyToMany(fetch = FetchType.EAGER) // Ensures roles are loaded with the user
//    @JoinTable(
//            name = "users_roles",
//            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
//    )
//    private Collection<Role> roles;
}
