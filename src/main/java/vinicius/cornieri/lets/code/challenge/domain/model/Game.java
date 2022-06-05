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
import javax.persistence.OneToOne;

@Entity
@Setter
@Getter
@ToString
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PLAYER_ID", nullable = false)
    private Player player;

    @Column(name = "CREATED_AT", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "FINISHED_AT")
    private ZonedDateTime finishedAt;

    @Column(name = "FINISHED")
    private boolean finished;

    @Column(name = "FAILURES_COUNT")
    private int failuresCount;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "CURRENT_ROUND_ID")
    @ToString.Exclude
    private Round currentRound;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Game game = (Game) o;

        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
