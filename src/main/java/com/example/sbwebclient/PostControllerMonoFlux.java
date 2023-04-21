package com.example.sbwebclient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/mono-flux/posts")
@RequiredArgsConstructor
public class PostControllerMonoFlux {

    private final PostWebClient postWebClient;

    @GetMapping
    public Flux<PostDto> getAllPosts() {
        return postWebClient.getAllPosts();
    }

    @GetMapping("/{id}")
    public Mono<PostDto> getPostById(@PathVariable int id) {
        return postWebClient.getPostById(id);
    }

    @PostMapping
    public Mono<PostDto> createPost(@RequestBody PostDto postDto) {
        return postWebClient.createPost(postDto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePostById(@PathVariable int id) {
        return postWebClient.deletePostById(id);
    }
}
