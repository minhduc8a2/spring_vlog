package com.ducco.vlog;

import com.ducco.vlog.models.Post;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
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

class VlogApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	private PostHelper postHelper;

	@BeforeEach
	void setUp() {
		postHelper = new PostHelper();
	}



	@Test
	void shouldReturnGreetingMessage() {
		ResponseEntity<String> response = restTemplate.getForEntity("/",String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		String message = response.getBody();
		assertThat(message).isEqualTo("Hello World!");
	}

	@Test
	void shouldReturnAPost(){
		Post firstSamplePost = postHelper.getFirstPost();
		ResponseEntity<String> response = restTemplate.getForEntity("/posts/"+firstSamplePost.getId().toString(),String.class);
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
	void shouldNotReturnAPostWithUnknownId(){
		ResponseEntity<String> response = restTemplate.getForEntity("/posts/"+postHelper.getUnknownId(),String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	void shouldReturnListOfPosts(){
		List<Post> samplePosts = postHelper.getPosts();
		ResponseEntity<String> response = restTemplate.getForEntity("/posts",String.class);
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
	@DirtiesContext
	void shouldReturnLocationOfNewCreatedCard(){
		Post samplePost = postHelper.getNewPost();


		ResponseEntity<String> response = restTemplate.postForEntity("/posts",samplePost,String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isBlank();

		URI location = response.getHeaders().getLocation();

		ResponseEntity<String> locationResponse = restTemplate.getForEntity(location,String.class);
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
	void shouldUpdateExistingPost(){
		Post needUpdatingPost = postHelper.getNeedUpdatingPost();
		HttpEntity<Post> request = new HttpEntity<>(needUpdatingPost);
		ResponseEntity<Void> response = restTemplate.exchange("/posts/"+needUpdatingPost.getId().toString(), HttpMethod.PUT,request,Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> updatedResponse = restTemplate.getForEntity("/posts/"+needUpdatingPost.getId().toString(),String.class);
		assertThat(updatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(updatedResponse.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id.longValue()).isEqualTo(needUpdatingPost.getId());

		String title = documentContext.read("$.title");
		assertThat(title).isEqualTo(needUpdatingPost.getTitle());

		String content = documentContext.read("$.content");
		assertThat(content).isEqualTo(needUpdatingPost.getContent());


	}

	@Test
	@DirtiesContext
	void shouldDeleteExistingPost(){
		Post samplePost = postHelper.getFirstPost();
		HttpEntity<Void> request = new HttpEntity<>(null);
		ResponseEntity<Void> response = restTemplate.exchange("/posts/"+samplePost.getId().toString(),HttpMethod.DELETE,request,Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		ResponseEntity<String> deletedResponse = restTemplate.getForEntity("/posts/"+samplePost.getId().toString(),String.class);
		assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

}
