package com.moviequiz.service;

import com.moviequiz.domain.Actor;
import com.moviequiz.dto.ActorDto;
import com.moviequiz.dto.ActorRequest;
import com.moviequiz.exception.ResourceNotFoundException;
import com.moviequiz.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActorService {

    private final ActorRepository actorRepository;

    public List<ActorDto> findAll() {
        return actorRepository.findAll().stream().map(this::toDto).toList();
    }

    public ActorDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public List<ActorDto> search(String name) {
        return actorRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDto).toList();
    }

    @Transactional
    public ActorDto create(ActorRequest request) {
        Actor actor = Actor.builder()
                .name(request.getName())
                .birthYear(request.getBirthYear())
                .deathYear(request.getDeathYear())
                .build();
        return toDto(actorRepository.save(actor));
    }

    @Transactional
    public ActorDto update(Long id, ActorRequest request) {
        Actor actor = getOrThrow(id);
        actor.setName(request.getName());
        actor.setBirthYear(request.getBirthYear());
        actor.setDeathYear(request.getDeathYear());
        return toDto(actorRepository.save(actor));
    }

    @Transactional
    public void delete(Long id) {
        if (!actorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Actor " + id + " not found");
        }
        actorRepository.deleteById(id);
    }

    private Actor getOrThrow(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor " + id + " not found"));
    }

    private ActorDto toDto(Actor actor) {
        return ActorDto.builder()
                .id(actor.getId())
                .name(actor.getName())
                .birthYear(actor.getBirthYear())
                .deathYear(actor.getDeathYear())
                .build();
    }
}
