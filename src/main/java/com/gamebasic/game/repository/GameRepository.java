package com.gamebasic.game.repository;

import com.gamebasic.game.entity.Game;
import com.gamebasic.runcard.entity.RunCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAllByOrderByIdDesc();
}
