package com.ducco.vlog.services;

import com.ducco.vlog.models.Post;
import com.ducco.vlog.models.User;
import com.ducco.vlog.repositories.PostRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private UserService userService;


    public Post getPostById(long id) {
        System.out.println("service id: "+id);
        return postRepo.findById(id).orElse(null);
    }

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepo.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));
    }

    public Post createPost(Post newPost,String email) {
        User currentUser = userService.findByEmail(email);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found");
        }
        return postRepo.save(new Post(null, newPost.getTitle(), newPost.getContent(), currentUser));
    }

    public void updatePost(Post needUpdatingPost,long postId) {
        Post currentPost = postRepo.findById(postId).orElse(null);
        if (currentPost == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Post not found");
        }
        currentPost.setTitle(needUpdatingPost.getTitle());
        currentPost.setContent(needUpdatingPost.getContent());
        postRepo.save(currentPost);
    }

    public void deletePost(Long postId) {
        postRepo.deleteById(postId);

    }




}
