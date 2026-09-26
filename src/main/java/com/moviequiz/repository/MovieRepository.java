package com.moviequiz.repository;

import com.moviequiz.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    @Query(value = """
            SELECT * FROM movie
            WHERE num_votes >= :minVotes
              AND release_year BETWEEN :minYear AND :maxYear
            ORDER BY RAND()
            LIMIT 1
            """, nativeQuery = true)
    Optional<Movie> findRandomMovie(@Param("minYear") int minYear,
                                     @Param("maxYear") int maxYear,
                                     @Param("minVotes") int minVotes);

    @Query(value = """
            SELECT title FROM movie
            WHERE id <> :excludeId
            ORDER BY RAND()
            LIMIT :count
            """, nativeQuery = true)
    List<String> findRandomTitlesExcluding(@Param("excludeId") Long excludeId,
                                            @Param("count") int count);
}
