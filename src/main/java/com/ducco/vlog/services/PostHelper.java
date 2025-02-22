package com.ducco.vlog.services;

import com.ducco.vlog.models.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PostHelper {

    List<Post> posts = new ArrayList<Post>(Arrays.asList(
            new Post(1L,"Post c","This is the content of post 1",null ),
            new Post(2L,"Post a","This is the content of post 2",null  ),
            new Post(3L,"Post f","This is the content of post 3",null  ),
            new Post(4L,"Post e","This is the content of post 4",null  )
    ));
    public Long getUnknownId(){
        return 0L;
    }
    public Post getFirstPost(){
        return posts.getFirst();
    }
    public Post getNewPost(){
        return new Post(5L,"Post k","This is the content of post 5",null  );
    }
    public Post getNeedUpdatingPost(){
        return new Post(1L,"Post c updated","This is the content of post 1 updated" ,null );
    }
    public  List<Post> getPosts(){
        return posts;
    }
}
