package com.irigoyen.challenge.minesweeper.repository;

import com.irigoyen.challenge.minesweeper.model.Board;
import com.irigoyen.challenge.minesweeper.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}
