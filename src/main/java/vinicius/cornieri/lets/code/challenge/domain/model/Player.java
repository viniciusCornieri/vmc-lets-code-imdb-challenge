package vinicius.cornieri.lets.code.challenge.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@ToString
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_GAME_ID")
    @ToString.Exclude
    private Game currentGame;

    @Column(name = "NICKNAME", nullable = false)
    private String nickname;

    @Column(name = "API_KEY", nullable = false)
    private String apiKey;

    @Column(name = "SCORE", nullable = false)
    private int score = 0;

    public void incrementScore(int byNewScore) {
        this.score += byNewScore;
    }

}
