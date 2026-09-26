package com.moviequiz.repository;

import com.moviequiz.domain.MovieActor;
import com.moviequiz.domain.MovieActorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieActorRepository extends JpaRepository<MovieActor, MovieActorId> {

    List<MovieActor> findByMovie_IdOrderByImportanceAsc(Long movieId);

    List<MovieActor> findByActor_Id(Long actorId);

    /**
     * Picks a random movie that has at least {@code minCastSize} credited
     * cast members within the given year/vote filters - used for the
     * "which movie starred both of these actors" question type.
     */
    @Query(value = """
            SELECT ma.movie_id
            FROM movie_actor ma
            JOIN movie m ON m.id = ma.movie_id
            WHERE m.num_votes >= :minVotes
              AND m.release_year BETWEEN :minYear AND :maxYear
            GROUP BY ma.movie_id
            HAVING COUNT(DISTINCT ma.actor_id) >= :minCastSize
            ORDER BY RAND()
            LIMIT 1
            """, nativeQuery = true)
    Optional<Long> findRandomMovieIdWithCastSizeAtLeast(@Param("minYear") int minYear,
                                                         @Param("maxYear") int maxYear,
                                                         @Param("minVotes") int minVotes,
                                                         @Param("minCastSize") int minCastSize);
}
