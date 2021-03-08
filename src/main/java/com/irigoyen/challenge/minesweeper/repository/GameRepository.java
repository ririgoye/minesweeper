package com.irigoyen.challenge.minesweeper.repository;

import com.irigoyen.challenge.minesweeper.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByUserIdAndStatusInOrderByStartTimeDesc(Long userId, List<Game.Status> status);
}
