package com.example.sbwebclient;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(PostControllerMonoFlux.class)
class PostControllerMonoFluxTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PostWebClient postWebClient;

    @Test
    public void getPostById_WhenFound_ShouldReturnPostDto() {
        int postId = 1;
        PostDto expectedPostDto = new PostDto(1, 1, "title", "body");
        when(postWebClient.getPostById(postId)).thenReturn(Mono.just(expectedPostDto));

        webTestClient.get().uri("/api/v1/mono-flux/posts/{id}", postId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"id\":1,\"userId\":1,\"title\":\"title\",\"body\":\"body\"}");
    }

    @Test
    public void getPostById_WhenNotFound_ShouldReturnNotFoundStatus() {
        int postId = 999;
        when(postWebClient.getPostById(postId)).thenThrow(WebClientResponseException.NotFound.class);

        webTestClient.get().uri("/api/v1/mono-flux/posts/{id}", postId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").doesNotExist();  // Expecting the message to not exist
        // because we throw WebClientResponseException.NotFound => here we not specify error message , that why we do not check it here
        // if we throw with exception message then we can check the message something like this:
//                .jsonPath("$.message").value(Matchers.containsString("404 Not Found from GET https://jsonplaceholder.typicode.com/posts/999"));
    }



    @Test
    public void testGetAllPosts_ShouldReturnListOfPosts() {
        // Arrange
        List<PostDto> expectedPosts = Arrays.asList(
                new PostDto(1, 1, "Title 1", "Body 1"),
                new PostDto(2, 1, "Title 2", "Body 2"),
                new PostDto(3, 1, "Title 3", "Body 3")
        );
        when(postWebClient.getAllPosts()).thenReturn(Flux.fromIterable(expectedPosts));

        // Act
        FluxExchangeResult<PostDto> result = webTestClient.get().uri("/api/v1/mono-flux/posts")
                .exchange()
                .expectStatus().isOk()
                .returnResult(PostDto.class);

        // Assert
        List<PostDto> actualPosts = result.getResponseBody().collectList().block();
        assertThat(expectedPosts.size()).isEqualTo(actualPosts.size());
    }

    @Test
    public void getAllPosts_WhenEmptyResponse_ShouldReturnEmptyList() {
        // Arrange
        when(postWebClient.getAllPosts()).thenReturn(Flux.empty());

        // Act
        FluxExchangeResult<PostDto> result = webTestClient.get().uri("/api/v1/mono-flux/posts")
                .exchange()
                .expectStatus().isOk()
                .returnResult(PostDto.class);

        // Assert
        List<PostDto> actualPosts = result.getResponseBody().collectList().block();
        assertThat(actualPosts).isEmpty();
    }

    @Test
    public void createPost_ShouldReturnCreatedPost() {
        // Prepare test data
        PostDto requestDto = new PostDto(1,1, "Test Title", "Test Body");

        // Set up mock behavior
        when(postWebClient.createPost(requestDto)).thenReturn(Mono.just(requestDto));

        // Perform the request and validate the response
        webTestClient.post().uri("/api/v1/mono-flux/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), PostDto.class)
                .exchange()
                .expectStatus().isOk();

        verify(postWebClient).createPost(requestDto);
    }

    @Test
    public void deletePostById_WhenFound_ShouldReturnOkStatus() {
        int postId = 1;
        when(postWebClient.deletePostById(postId)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/mono-flux/posts/{id}", postId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void deletePostById_WhenNotFound_ShouldReturnNotFoundStatus() {
        int postId = 999;
        when(postWebClient.deletePostById(postId)).thenThrow(WebClientResponseException.NotFound.class);

        webTestClient.delete().uri("/api/v1/mono-flux/posts/{id}", postId)
                .exchange()
                .expectStatus().isNotFound();
    }
}