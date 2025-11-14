package com.seongsikchoi.weddinginvitationserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guestbook")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guestbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "content", length = 200)
    private String content;

    @Column(name = "password", length = 20)
    private String password;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "valid")
    @Builder.Default
    private Boolean valid = true;
}

