package vinicius.cornieri.lets.code.challenge.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

}
