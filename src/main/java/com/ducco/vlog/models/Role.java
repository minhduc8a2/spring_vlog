package com.ducco.vlog.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "roles")
public class Role {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @Column(nullable = false, unique = true)
      private String name;

      @ManyToMany(mappedBy = "roles")
      private Collection<User> users;

      @ManyToMany
      @JoinTable(
              name = "roles_privileges",
              joinColumns = @JoinColumn(columnDefinition = "role_id",referencedColumnName = "id"),
              inverseJoinColumns = @JoinColumn(columnDefinition = "privilege_id",referencedColumnName = "id")
      )
      private Collection<Privilege> privileges;

    public Role(String name) {
        this.name = name;
    }

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//
//    @ManyToMany(mappedBy = "roles")
//    private Collection<User> users;
//
//    @ManyToMany
//    @JoinTable(
//            name = "roles_privileges",
//            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id")
//    )
//    private Collection<Privilege> privileges;
//
//    public Role(String name) {
//        this.name = name;
//    }
}
