package com.irigoyen.challenge.minesweeper.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String image;
    @Column
    private Integer gamesPlayed;
    @Column
    private Integer gamesLost;
    @Column
    private Integer gamesWon;
}
