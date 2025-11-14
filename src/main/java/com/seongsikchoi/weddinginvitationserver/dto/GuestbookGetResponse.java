package com.seongsikchoi.weddinginvitationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookGetResponse {
    private List<GuestbookPostForGet> posts;
    private Integer total;
}

