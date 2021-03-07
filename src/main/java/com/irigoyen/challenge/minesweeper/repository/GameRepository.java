package com.irigoyen.challenge.minesweeper.repository;

import com.irigoyen.challenge.minesweeper.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}
