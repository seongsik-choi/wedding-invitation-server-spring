package com.seongsikchoi.weddinginvitationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookJson {
    private Integer id;
    private String name;
    private String content;
    private String password;
    private Long timestamp;
    private Boolean valid;
}

