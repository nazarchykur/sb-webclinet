package com.example.sbwebclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PostWebClient {

    private final WebClient webClient;

    public PostWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    public Flux<PostDto> getAllPosts() {
        return webClient
                .get()
                .uri("/posts")
                .retrieve()
                .bodyToFlux(PostDto.class);
    }

    public Mono<PostDto> getPostById(int id) {
        return webClient
                .get()
                .uri("/posts/{id}", id)
                .retrieve()
                .bodyToMono(PostDto.class);
    }

    public Mono<PostDto> createPost(PostDto postDto) {
        return webClient
                .post()
                .uri("/posts")
                .body(Mono.just(postDto), PostDto.class)
                .retrieve()
                .bodyToMono(PostDto.class);
    }

    public Mono<Void> deletePostById(int id) {
        return webClient
                .delete()
                .uri("/posts/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
