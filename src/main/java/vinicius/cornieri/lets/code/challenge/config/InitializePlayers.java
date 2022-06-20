package vinicius.cornieri.lets.code.challenge.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;
import vinicius.cornieri.lets.code.challenge.domain.service.PlayerService;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class InitializePlayers implements ApplicationListener<ContextStartedEvent> {

    private final PlayerService playerService;

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        playerService.createNewPlayer("player_one", "player_one", "GAME");
        playerService.createNewPlayer("scd_player", "scd_player", "GAME");
        playerService.createNewPlayer("visitor", "visitor", "VISITOR");
        log.info("created default players");
    }

}
