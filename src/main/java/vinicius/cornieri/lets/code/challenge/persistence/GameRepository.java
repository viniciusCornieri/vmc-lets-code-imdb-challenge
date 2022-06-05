package vinicius.cornieri.lets.code.challenge.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

}
