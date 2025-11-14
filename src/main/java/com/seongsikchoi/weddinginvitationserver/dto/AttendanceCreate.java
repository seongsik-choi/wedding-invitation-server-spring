package com.seongsikchoi.weddinginvitationserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCreate {
    @NotNull
    private String side;
    
    @NotNull
    private String name;
    
    @NotNull
    private String meal;
    
    @NotNull
    private Integer count;
}

