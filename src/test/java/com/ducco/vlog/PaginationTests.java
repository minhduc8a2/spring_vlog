package com.ducco.vlog;


import com.ducco.vlog.models.Post;
import com.ducco.vlog.services.PostHelper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaginationTests {
    String[] userCredential = new String[]{"user@gmail.com","user"};
    @Autowired
    private TestRestTemplate restTemplate;

    private final PostHelper postHelper = new PostHelper();

    @Test
    void shouldReturnAPageOfPosts() {
        ResponseEntity<String> response = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).getForEntity("/posts?page=0&size=2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(2);
    }

    @Test
    void shouldReturnAPageOfPostsWithoutParameters() {
        ResponseEntity<String> response = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).getForEntity("/posts", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(postHelper.getPosts().size());
    }

    @Test
    void shouldReturnASortedPageOfPostsById() {
        List<Post> posts = postHelper.getPosts();
        ResponseEntity<String> response = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).getForEntity("/posts?sort=id,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(postHelper.getPosts().size());

        JSONArray idsJSon = documentContext.read("$.[*].id");
        List<Long> ids = idsJSon.stream().map(idJson -> Long.parseLong(idJson.toString())).collect(Collectors.toList());
        Long[] expectedIds = posts.stream().map(Post::getId).sorted(Comparator.reverseOrder()).toArray(Long[]::new);
        assertThat(ids).containsExactly(expectedIds);
    }

    @Test
    void shouldReturnASortedPageOfPostsByTitle() {
        List<Post> posts = postHelper.getPosts();
        ResponseEntity<String> response = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).getForEntity("/posts?sort=title,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(postHelper.getPosts().size());

        JSONArray titlesJSon = documentContext.read("$.[*].title");
        List<String> titles = titlesJSon.stream().map(Object::toString).toList();
        String[] expectedIds = posts.stream().map(Post::getTitle).sorted(Comparator.reverseOrder()).toArray(String[]::new);
        assertThat(titles).containsExactly(expectedIds);
    }



}
