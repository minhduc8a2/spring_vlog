package com.ducco.vlog;

import com.ducco.vlog.models.Post;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PostHelper {

    List<Post> posts = new ArrayList<Post>(Arrays.asList(
            new Post(1L,"Post c","This is the content of post 1" ),
            new Post(2L,"Post a","This is the content of post 2" ),
            new Post(3L,"Post b","This is the content of post 3" )
    ));
    public Long getUnknownId(){
        return 0L;
    }
    public Post getFirstPost(){
        return posts.getFirst();
    }
    public Post getNewPost(){
        return new Post(4L,"Post d","This is the content of post 4" );
    }
    public Post getNeedUpdatingPost(){
        return new Post(1L,"Post c updated","This is the content updated" );
    }
    public  List<Post> getPosts(){
        return posts;
    }
}
