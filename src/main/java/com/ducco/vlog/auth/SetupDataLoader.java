package com.ducco.vlog.auth;


import com.ducco.vlog.models.Privilege;
import com.ducco.vlog.models.Role;
import com.ducco.vlog.models.User;
import com.ducco.vlog.repositories.PrivilegeRepository;
import com.ducco.vlog.repositories.RoleRepository;

import com.ducco.vlog.services.PostHelper;
import com.ducco.vlog.services.PostService;
import com.ducco.vlog.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PostService postService;

    private PostHelper postHelper = new PostHelper();

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege deletePrivilege
                = createPrivilegeIfNotFound("DELETE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege, deletePrivilege);
        List<Privilege> staffPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_STAFF", staffPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        User admin = new User(null, "Admin", "Admin", "admin@gmail.com", passwordEncoder.encode("admin"), true, true, Collections.singletonList(adminRole),null);
        userService.createUser(admin);


        postService.createPost(postHelper.getPosts().get(0),admin.getEmail());
        postService.createPost(postHelper.getPosts().get(1),admin.getEmail());

        Role staffRole = roleRepository.findByName("ROLE_STAFF");
        User staff = new User(null, "Staff", "Staff", "staff@gmail.com", passwordEncoder.encode("staff"), true, true, Collections.singletonList(staffRole),null);
        userService.createUser(staff);

        Role userRole = roleRepository.findByName("ROLE_USER");
        User normalUser = new User(null, "User", "User", "user@gmail.com", passwordEncoder.encode("user"), true, true, Collections.singletonList(userRole),null);
        userService.createUser(normalUser);

        postService.createPost(postHelper.getPosts().get(2),normalUser.getEmail());
        postService.createPost(postHelper.getPosts().get(3),normalUser.getEmail());

        User normalUser2 = new User(null, "User2", "User2", "user2@gmail.com", passwordEncoder.encode("user2"), true, true, Collections.singletonList(userRole),null);
        userService.createUser(normalUser2);


        alreadySetup = true;
    }


    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}