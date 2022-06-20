package vinicius.cornieri.lets.code.challenge.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@NoArgsConstructor
public class PlayerRole {

    public PlayerRole(Player player, String role) {
        this.player = player;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "PLAYER_ID")
    @ToString.Exclude
    private Player player;

    @Getter
    @Column(name = "ROLE", nullable = false)
    private String role;


}
