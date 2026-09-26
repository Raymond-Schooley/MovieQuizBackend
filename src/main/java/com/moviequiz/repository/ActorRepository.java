package com.moviequiz.repository;

import com.moviequiz.domain.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    List<Actor> findByNameContainingIgnoreCase(String name);

    @Query(value = """
            SELECT name FROM actor
            WHERE id <> :excludeId
            ORDER BY RAND()
            LIMIT :count
            """, nativeQuery = true)
    List<String> findRandomNamesExcluding(@Param("excludeId") Long excludeId,
                                           @Param("count") int count);
}
