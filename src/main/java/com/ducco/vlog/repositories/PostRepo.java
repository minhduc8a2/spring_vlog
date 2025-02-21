package com.ducco.vlog.repositories;

import com.ducco.vlog.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PostRepo extends JpaRepository<Post, Long>, PagingAndSortingRepository<Post, Long> {


}
