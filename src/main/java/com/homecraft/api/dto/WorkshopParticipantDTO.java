package com.homecraft.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkshopParticipantDTO {
    private int sno;
    private String name;
    private String email;
}