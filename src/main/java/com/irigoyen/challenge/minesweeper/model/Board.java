package com.irigoyen.challenge.minesweeper.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long userId;
    @Column
    private Integer rows;
    @Column
    private Integer columns;
    /**
     * Game board layout. This will not change once defined
     * 0->8 = number of mines around that cell. (0=empty)
     * M = mine.
     */
    @Column
    private String layout;
    /**
     * Status of each cell. This will be modified when the user click the grid.
     * 
     * H = hidden
     * F = flag
     * X = explosion
     * Space = visible
     */
    @Column
    private String state;
    /**
     * won | loosed | paused
     */
    @Column
    private Status status;
    /**
     * When game was stared or resumed
     */
    @Column
    private LocalDateTime startTime;
    /**
     * Time since game started.
     */
    @Column
    private Integer elapsedTime;

    public int getSize() {
        return rows * columns;
    }

    public enum Status {
        STARTED, WON, LOST, PAUSED
    }
}
