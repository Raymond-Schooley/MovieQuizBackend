package com.moviequiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActorRequest {

    @NotBlank
    private String name;

    private Integer birthYear;

    private Integer deathYear;
}
