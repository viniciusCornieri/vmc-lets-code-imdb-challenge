package vinicius.cornieri.lets.code.challenge.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "SCORE", nullable = false)
    private int score = 0;

    @OneToMany(mappedBy="player", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<PlayerRole> roles;

    public void addNewRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }

        roles.add(new PlayerRole(this, role));
    }

    public void incrementScore(int byNewScore) {
        this.score += byNewScore;
    }

}
