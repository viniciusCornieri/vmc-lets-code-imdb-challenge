package vinicius.cornieri.lets.code.challenge.domain.service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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
    @Mapping(target = "wasRight", source = "lastRound.wasAnsweredCorrectly")
    GameChooseResponseDto fromGameAndLastRound(Game game, Round lastRound, ChoiceDto correctAnswer);

    @AfterMapping
    default void afterMapping(@MappingTarget GameChooseResponseDto dto){
        if (Boolean.TRUE.equals(dto.getFinished())) {
            dto.setNextRound(null);
        }
    }
}
