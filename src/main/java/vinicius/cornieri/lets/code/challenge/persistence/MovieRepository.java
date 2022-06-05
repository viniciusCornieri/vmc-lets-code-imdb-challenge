package vinicius.cornieri.lets.code.challenge.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

}
