package vinicius.cornieri.lets.code.challenge.domain.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.ChoiceDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseResponseDto;

@Mapper
public interface GameChooseResponseDtoMapper {

    GameChooseResponseDtoMapper INSTANCE = Mappers.getMapper( GameChooseResponseDtoMapper.class );

    @Mapping(target = "nextRound", source = "game.currentRound")
    @Mapping(target = "roundResult", source = "lastRound")
    @Mapping(target = "roundResult.correctAnswer", source = "correctAnswer")
    @Mapping(target = "wasRight", source = "game.currentRound.wasAnsweredCorrectly")
    GameChooseResponseDto fromGameAndLastRound(Game game, Round lastRound, ChoiceDto correctAnswer);
}
