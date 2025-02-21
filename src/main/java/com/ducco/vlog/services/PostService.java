package com.ducco.vlog.services;

import com.ducco.vlog.models.Post;
import com.ducco.vlog.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepo postRepo;


    public Post getPostById(long id) {
        System.out.println("service id: "+id);
        return postRepo.findById(id).orElse(null);
    }

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepo.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));
    }

    public Post createPost(Post newPost) {
        return postRepo.save(newPost);
    }

    public void updatePost(Post needUpdatingPost) {
        postRepo.save(needUpdatingPost);
    }

    public void deletePost(Long postId) {
        postRepo.deleteById(postId);
    }
}
