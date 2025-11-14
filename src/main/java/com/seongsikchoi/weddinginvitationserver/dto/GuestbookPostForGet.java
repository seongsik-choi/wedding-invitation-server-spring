package com.seongsikchoi.weddinginvitationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookPostForGet {
    private Integer id;
    private String name;
    private String content;
    private Long timestamp;
}

