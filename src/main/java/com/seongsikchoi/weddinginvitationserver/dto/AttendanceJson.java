package com.seongsikchoi.weddinginvitationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceJson {
    private Integer id;
    private String side;
    private String name;
    private String meal;
    private Integer count;
    private Long timestamp;
}

