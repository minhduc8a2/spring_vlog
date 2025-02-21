package com.ducco.vlog.controllers;

import com.ducco.vlog.models.Post;
import com.ducco.vlog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;



    @GetMapping()
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts) ;
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
    public ResponseEntity<Void> createPost(@RequestBody Post post, UriComponentsBuilder ucBuilder) {
        Post newPost = new Post(null, post.getTitle(), post.getContent());
        Post savedPost = postService.createPost(newPost);
        URI location = ucBuilder.path("posts/{id}").buildAndExpand(savedPost.getId()).toUri();
        return ResponseEntity.created(location).build();

    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@RequestBody Post post,@PathVariable Long postId) {
        Post needUpdatingPost = new Post(postId, post.getTitle(), post.getContent());
        postService.updatePost(needUpdatingPost);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }







}
