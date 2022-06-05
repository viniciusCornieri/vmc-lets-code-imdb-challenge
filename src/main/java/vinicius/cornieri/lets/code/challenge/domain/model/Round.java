package vinicius.cornieri.lets.code.challenge.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@ToString
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROUND_NUMBER", nullable = false)
    private int roundNumber;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "GAME_ID", nullable = false)
    private Game game;

    @Column(name = "CREATED_AT", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @ManyToOne
    @JoinColumn(name = "FIRST_MOVIE_OPTION_ID", nullable = false)
    private Movie firstMovieOption;

    @ManyToOne
    @JoinColumn(name = "SECOND_MOVIE_OPTION_ID", nullable = false)
    private Movie secondMovieOption;

    @Column(name = "WAS_ANSWERED_CORRECTLY")
    private Boolean wasAnsweredCorrectly;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Round round = (Round) o;

        return Objects.equals(id, round.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
