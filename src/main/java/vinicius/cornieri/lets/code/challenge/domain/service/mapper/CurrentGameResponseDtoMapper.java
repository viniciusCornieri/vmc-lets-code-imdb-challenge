package vinicius.cornieri.lets.code.challenge.domain.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.CurrentGameResponseDto;

@Mapper
public interface CurrentGameResponseDtoMapper {

    CurrentGameResponseDtoMapper INSTANCE = Mappers.getMapper( CurrentGameResponseDtoMapper.class );

    @Mapping(target = "round", source = "currentRound")
    CurrentGameResponseDto fromGame(Game game);
}
