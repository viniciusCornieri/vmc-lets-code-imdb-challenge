package vinicius.cornieri.lets.code.challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vinicius.cornieri.lets.code.challenge.config.InitializePlayers;

import javax.annotation.PostConstruct;

@Component
public class InitializeTestPlayers {

    @Autowired
    private InitializePlayers initializePlayers;

    @PostConstruct
    void init() {
        initializePlayers.onApplicationEvent(null);
    }
}
