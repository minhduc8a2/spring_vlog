package com.ducco.vlog.services;

import com.ducco.vlog.models.Post;
import com.ducco.vlog.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Post> getAllPosts() {
        return postRepo.findAll();
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
