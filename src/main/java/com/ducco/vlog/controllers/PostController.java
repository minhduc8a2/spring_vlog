package com.ducco.vlog.controllers;

import com.ducco.vlog.models.Post;
import com.ducco.vlog.models.User;
import com.ducco.vlog.services.PostSecurityService;
import com.ducco.vlog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostSecurityService postSecurityService;


    @GetMapping()
    public ResponseEntity<List<Post>> getAllPosts(Pageable pageable) {
        Page<Post> page = postService.getAllPosts(pageable);
        List<Post> posts = page.getContent();
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        System.out.println(postId);
        Post post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping()
    public ResponseEntity<Void> createPost(@RequestBody Post post, UriComponentsBuilder ucBuilder, Principal principal) {
        Post savedPost = postService.createPost(post,principal.getName());
        URI location = ucBuilder.path("posts/{id}").buildAndExpand(savedPost.getId()).toUri();
        return ResponseEntity.created(location).build();

    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_STAFF') or @postSecurityService.isPostOwner(#postId, principal.username)")
    public ResponseEntity<Void> updatePost(@RequestBody Post post, @PathVariable Long postId, Principal principal) {
        postService.updatePost(post,postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @postSecurityService.isPostOwner(#postId, principal.username)")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Principal principal) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }


}
