package vinicius.cornieri.lets.code.challenge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.TimeZone;

@Configuration
@Slf4j
public class TimeZoneConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${default.timezone:America/Sao_Paulo}")
    private String defaultTimezone;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Setting system default timeZone to {}", defaultTimezone);
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimezone));
    }

}
