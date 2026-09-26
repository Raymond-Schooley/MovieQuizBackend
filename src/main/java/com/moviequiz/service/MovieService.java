package com.moviequiz.service;

import com.moviequiz.domain.Movie;
import com.moviequiz.domain.MovieActor;
import com.moviequiz.dto.CastMemberDto;
import com.moviequiz.dto.MovieDto;
import com.moviequiz.dto.MovieRequest;
import com.moviequiz.exception.ResourceNotFoundException;
import com.moviequiz.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;

    public List<MovieDto> findAll() {
        return movieRepository.findAll().stream().map(this::toDto).toList();
    }

    public MovieDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public List<MovieDto> search(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream().map(this::toDto).toList();
    }

    @Transactional
    public MovieDto create(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .releaseYear(request.getReleaseYear())
                .runtimeMinutes(request.getRuntimeMinutes())
                .averageRating(request.getAverageRating())
                .numVotes(request.getNumVotes())
                .build();
        return toDto(movieRepository.save(movie));
    }

    @Transactional
    public MovieDto update(Long id, MovieRequest request) {
        Movie movie = getOrThrow(id);
        movie.setTitle(request.getTitle());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setRuntimeMinutes(request.getRuntimeMinutes());
        movie.setAverageRating(request.getAverageRating());
        movie.setNumVotes(request.getNumVotes());
        return toDto(movieRepository.save(movie));
    }

    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie " + id + " not found");
        }
        movieRepository.deleteById(id);
    }

    private Movie getOrThrow(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + id + " not found"));
    }

    private MovieDto toDto(Movie movie) {
        List<CastMemberDto> cast = movie.getCast().stream()
                .sorted((a, b) -> a.getImportance().compareTo(b.getImportance()))
                .map(this::toCastMemberDto)
                .toList();
        return MovieDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .releaseYear(movie.getReleaseYear())
                .runtimeMinutes(movie.getRuntimeMinutes())
                .averageRating(movie.getAverageRating())
                .numVotes(movie.getNumVotes())
                .cast(cast)
                .build();
    }

    private CastMemberDto toCastMemberDto(MovieActor movieActor) {
        return CastMemberDto.builder()
                .actorId(movieActor.getActor().getId())
                .actorName(movieActor.getActor().getName())
                .characterName(movieActor.getCharacterName())
                .importance(movieActor.getImportance())
                .build();
    }
}
