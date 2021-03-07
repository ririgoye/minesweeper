package com.irigoyen.challenge.minesweeper.model;

import com.irigoyen.challenge.minesweeper.infrastructure.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long userId;
    @Column
    private Integer rows;
    @Column
    private Integer columns;
    @Column
    private Integer mines;
    /**
     * Game game layout. This will not change once defined
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
    private Long elapsedTime;

    public int getSize() {
        return rows * columns;
    }

    public void calculateElapsedTime() {
        Duration duration = Duration.between(getStartTime(), LocalDateTime.now());
        setElapsedTime(duration.toSeconds());
    }

    public void resetGame() {
        setStartTime(LocalDateTime.now());
        setStatus(Game.Status.STARTED);
        setElapsedTime(0l);
        //completely covered grid (Hidden cells)
        String state = new String(Utils.fillArray('H', getSize()));
        setState(state);
    }

    public enum Status {
        STARTED, WON, LOST, PAUSED
    }
}
