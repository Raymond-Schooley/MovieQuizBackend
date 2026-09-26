package com.moviequiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {
    private Long id;
    private String title;
    private Integer releaseYear;
    private Integer runtimeMinutes;
    private BigDecimal averageRating;
    private Integer numVotes;
    private List<CastMemberDto> cast;
}
