package com.moviequiz.controller;

import com.moviequiz.dto.ActorDto;
import com.moviequiz.dto.ActorRequest;
import com.moviequiz.service.ActorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @GetMapping
    public List<ActorDto> findAll(@RequestParam(required = false) String name) {
        return (name == null || name.isBlank()) ? actorService.findAll() : actorService.search(name);
    }

    @GetMapping("/{id}")
    public ActorDto findById(@PathVariable Long id) {
        return actorService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ActorDto> create(@Valid @RequestBody ActorRequest request) {
        ActorDto created = actorService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/actors/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ActorDto update(@PathVariable Long id, @Valid @RequestBody ActorRequest request) {
        return actorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        actorService.delete(id);
    }
}
