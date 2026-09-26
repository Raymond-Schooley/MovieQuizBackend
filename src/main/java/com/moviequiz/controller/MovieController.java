package com.moviequiz.controller;

import com.moviequiz.dto.MovieDto;
import com.moviequiz.dto.MovieRequest;
import com.moviequiz.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public List<MovieDto> findAll(@RequestParam(required = false) String title) {
        return (title == null || title.isBlank()) ? movieService.findAll() : movieService.search(title);
    }

    @GetMapping("/{id}")
    public MovieDto findById(@PathVariable Long id) {
        return movieService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MovieDto> create(@Valid @RequestBody MovieRequest request) {
        MovieDto created = movieService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/movies/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public MovieDto update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return movieService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        movieService.delete(id);
    }
}
