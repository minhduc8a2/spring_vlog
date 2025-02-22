package com.ducco.vlog;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class VlogSecurityTests {

    @Autowired
    private TestRestTemplate restTemplate;

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
    void shouldReturnPostsWithAuthentication() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("user@gmail.com","user").getForEntity("/posts",  String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(3);


    }

}
