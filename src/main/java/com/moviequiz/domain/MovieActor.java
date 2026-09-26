package com.moviequiz.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Join entity between {@link Movie} and {@link Actor} carrying the extra
 * facts a quiz question needs: how prominent the role was (lower
 * "importance" = more prominent / closer to top billing) and the character
 * name played.
 */
@Entity
@Table(name = "movie_actor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieActor {

    @EmbeddedId
    private MovieActorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movie")
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actor")
    @JoinColumn(name = "actor_id")
    private Actor actor;

    @Column(nullable = false)
    private Integer importance;

    @Column(name = "character_name")
    private String characterName;
}
