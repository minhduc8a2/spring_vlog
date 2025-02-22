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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class VlogSecurityTests {
    String[] userCredential = new String[]{"user@gmail.com","user"};
    String[] userCredential2 = new String[]{"user2@gmail.com","user2"};
    String[] adminCredential = new String[]{"admin@gmail.com","admin"};
    String[] staffCredential = new String[]{"staff@gmail.com","staff"};
    String[] fakeCredential = new String[]{"fake@gmail.com","fake"};
    @Autowired
    private TestRestTemplate restTemplate;
    private final PostHelper postHelper = new PostHelper();

    @Test
    void homeRouteShouldBeAllowedWithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello World!");
    }

    @Test
    void shouldNotReturnPostsWithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/posts",  String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldNotReturnPostsWithFakeAuthentication() {
        ResponseEntity<String> response = restTemplate.withBasicAuth(fakeCredential[0],fakeCredential[1]).getForEntity("/posts",  String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }



    void shouldReturnAPost(String[] credential){
        Post firstSamplePost = postHelper.getFirstPost();
        ResponseEntity<String> response = restTemplate.withBasicAuth(credential[0],credential[1]).getForEntity("/posts/"+firstSamplePost.getId().toString(),String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id.longValue()).isEqualTo(firstSamplePost.getId());

        String title = documentContext.read("$.title");
        assertThat(title).isEqualTo(firstSamplePost.getTitle());

        String content = documentContext.read("$.content");
        assertThat(content).isEqualTo(firstSamplePost.getContent());

    }

    @Test
    void shouldReturnAPost(){
        shouldReturnAPost(userCredential);
        shouldReturnAPost(staffCredential);
        shouldReturnAPost(adminCredential);
    }


    void shouldNotReturnAPostWithUnknownId(String[] credential){
        ResponseEntity<String> response = restTemplate.withBasicAuth(credential[0],credential[1]).getForEntity("/posts/"+postHelper.getUnknownId(),String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldNotReturnAPostWithUnknownId(){
        shouldNotReturnAPostWithUnknownId(userCredential);
        shouldNotReturnAPostWithUnknownId(staffCredential);
        shouldNotReturnAPostWithUnknownId(adminCredential);
    }


    void shouldReturnListOfPosts(String[] credential){
        List<Post> samplePosts = postHelper.getPosts();
        ResponseEntity<String> response = restTemplate.withBasicAuth(credential[0],credential[1]).getForEntity("/posts",String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int numberOfPosts = documentContext.read("$.length()");
        assertThat(numberOfPosts).isEqualTo(samplePosts.size());

        JSONArray idsJsonArray = documentContext.read("$..id");
        List<Long> ids = idsJsonArray.stream()
                .map(id -> Long.valueOf(id.toString()))
                .collect(Collectors.toList());

        Long[] expectedIds = samplePosts.stream()
                .map(Post::getId)
                .toArray(Long[]::new);
        assertThat(ids).containsExactlyInAnyOrder(expectedIds);

        JSONArray titles = documentContext.read("$..title");
        String[] expectedTitles = samplePosts.stream().map(Post::getTitle).toArray(String[]::new);
        assertThat(titles).containsExactlyInAnyOrder(expectedTitles);
    }

    @Test
    void shouldReturnListOfPosts(){
        shouldReturnListOfPosts(userCredential);
        shouldReturnListOfPosts(staffCredential);
        shouldReturnListOfPosts(adminCredential);
    }


    void shouldReturnLocationOfNewCreatedCard(String[] credential){
        Post samplePost = postHelper.getNewPost();
        ResponseEntity<String> response = restTemplate.withBasicAuth(credential[0],credential[1]).postForEntity("/posts",samplePost,String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isBlank();

        URI location = response.getHeaders().getLocation();

        ResponseEntity<String> locationResponse = restTemplate.withBasicAuth(credential[0],credential[1]).getForEntity(location,String.class);
        assertThat(locationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(locationResponse.getBody());

        Number id = documentContext.read("$.id");
        assertThat(id.longValue()).isEqualTo(samplePost.getId());

        String title = documentContext.read("$.title");
        assertThat(title).isEqualTo(samplePost.getTitle());

        String content = documentContext.read("$.content");
        assertThat(content).isEqualTo(samplePost.getContent());
    }

    @Test
    @DirtiesContext
    void shouldReturnLocationOfNewCreatedCardByUser(){
        shouldReturnLocationOfNewCreatedCard(userCredential);
    }

    @Test
    @DirtiesContext
    void shouldReturnLocationOfNewCreatedCardByStaff(){
        shouldReturnLocationOfNewCreatedCard(staffCredential);
    }

    @Test
    @DirtiesContext
    void shouldReturnLocationOfNewCreatedCardByAdmin(){
        shouldReturnLocationOfNewCreatedCard(adminCredential);
    }


    void shouldUpdateExistingPost(String[] credential, long postId){
        Post needUpdatingPost = postHelper.getNeedUpdatingPost();
        HttpEntity<Post> request = new HttpEntity<>(needUpdatingPost);
        ResponseEntity<Void> response = restTemplate.withBasicAuth(credential[0],credential[1]).exchange("/posts/"+postId, HttpMethod.PUT,request,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> updatedResponse = restTemplate.withBasicAuth(credential[0],credential[1]).getForEntity("/posts/"+postId,String.class);
        assertThat(updatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(updatedResponse.getBody());

        Number id = documentContext.read("$.id");
        assertThat(id.longValue()).isEqualTo(postId);

        String title = documentContext.read("$.title");
        assertThat(title).isEqualTo(needUpdatingPost.getTitle());

        String content = documentContext.read("$.content");
        assertThat(content).isEqualTo(needUpdatingPost.getContent());

    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingPostByUser(){
        shouldUpdateExistingPost(userCredential,3);
    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingPostByStaff(){
        shouldUpdateExistingPost(staffCredential,3);
    }

    @Test
    @DirtiesContext
    void shouldUpdateExistingPostByAdmin(){
        shouldUpdateExistingPost(adminCredential,3);
    }

    @Test
    void nonOwnerShouldNotAllowedToUpdatePost(){
        Post needUpdatingPost = postHelper.getNeedUpdatingPost();
        HttpEntity<Post> request = new HttpEntity<>(needUpdatingPost);
        ResponseEntity<Void> response = restTemplate.withBasicAuth(userCredential2[0],userCredential2[1]).exchange("/posts/1", HttpMethod.PUT,request,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }


    void shouldNotUpdateNonExistingPost(String[] credential, long postId){
        Post needUpdatingPost = postHelper.getNeedUpdatingPost();
        HttpEntity<Post> request = new HttpEntity<>(needUpdatingPost);
        ResponseEntity<Void> response = restTemplate.withBasicAuth(credential[0],credential[1]).exchange("/posts/"+postId, HttpMethod.PUT,request,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotUpdateNonExistingPost(){
        shouldNotUpdateNonExistingPost(userCredential,100);
        shouldNotUpdateNonExistingPost(staffCredential,100);
        shouldNotUpdateNonExistingPost(adminCredential,100);
    }





    void shouldDeleteExistingPost(String[] credential, long postId){
        HttpEntity<Void> request = new HttpEntity<>(null);
        ResponseEntity<Void> response = restTemplate.withBasicAuth(credential[0],credential[1]).exchange("/posts/"+postId,HttpMethod.DELETE,request,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<String> deletedResponse = restTemplate.withBasicAuth(credential[0],credential[1]).getForEntity("/posts/"+postId,String.class);
        assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    @DirtiesContext
    void shouldDeleteExistingPost(){
        shouldDeleteExistingPost(userCredential,3);
//        shouldDeleteExistingPost(staffCredential,100);
        shouldDeleteExistingPost(adminCredential,1);
    }

    @Test
    @DirtiesContext
    void adminCanDeleteAnOtherPost(){
        shouldDeleteExistingPost(adminCredential,3);
    }

    @Test
    @DirtiesContext
    void userCanNotDeleteAnOtherPost(){
        Long postId = postHelper.getFirstPost().getId();
        HttpEntity<Void> request = new HttpEntity<>(null);
        ResponseEntity<Void> response = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).exchange("/posts/"+postId,HttpMethod.DELETE,request,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<String> deletedResponse = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).getForEntity("/posts/"+postId,String.class);
        assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    @DirtiesContext
    void canNotDeleteNonExistingPost(){
        long postId = 100L;
        HttpEntity<Void> request = new HttpEntity<>(null);
        ResponseEntity<Void> response = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).exchange("/posts/"+postId,HttpMethod.DELETE,request,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<String> deletedResponse = restTemplate.withBasicAuth(userCredential[0],userCredential[1]).getForEntity("/posts/"+postId,String.class);
        assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }


















}
