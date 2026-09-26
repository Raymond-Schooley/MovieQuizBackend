package com.moviequiz.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovieRequest {

    @NotBlank
    private String title;

    @NotNull
    @Min(1888)
    private Integer releaseYear;

    @Min(1)
    private Integer runtimeMinutes;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal averageRating;

    @NotNull
    @Min(0)
    private Integer numVotes;
}
