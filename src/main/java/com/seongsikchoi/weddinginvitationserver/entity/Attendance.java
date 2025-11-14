package com.seongsikchoi.weddinginvitationserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "side", length = 10)
    private String side;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "meal", length = 20)
    private String meal;

    @Column(name = "count")
    private Integer count;

    @Column(name = "timestamp")
    private Long timestamp;
}

