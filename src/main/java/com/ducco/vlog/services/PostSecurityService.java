package com.ducco.vlog.services;

import com.ducco.vlog.models.Post;
import com.ducco.vlog.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PostSecurityService {

    @Autowired
    private PostRepo postRepo;

    public boolean isPostOwner(Long postId, String userEmail) {
        Post post = postRepo.findById(postId).orElse(null);
        if (post==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        if (!post.getUser().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return true;

    }
}
