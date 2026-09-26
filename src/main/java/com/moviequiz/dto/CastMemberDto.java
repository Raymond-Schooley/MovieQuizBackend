package com.moviequiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CastMemberDto {
    private Long actorId;
    private String actorName;
    private String characterName;
    private Integer importance;
}
