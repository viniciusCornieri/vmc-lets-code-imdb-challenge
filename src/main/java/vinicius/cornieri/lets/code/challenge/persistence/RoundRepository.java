package vinicius.cornieri.lets.code.challenge.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {

    int countByGame(Game game);

}
