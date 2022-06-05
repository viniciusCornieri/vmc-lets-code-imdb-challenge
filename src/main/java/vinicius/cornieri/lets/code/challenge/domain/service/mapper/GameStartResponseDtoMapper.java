package vinicius.cornieri.lets.code.challenge.domain.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameStartResponseDto;

@Mapper
public interface GameStartResponseDtoMapper {

    GameStartResponseDtoMapper INSTANCE = Mappers.getMapper( GameStartResponseDtoMapper.class );

    @Mapping(target = ".", source = "currentRound")
    GameStartResponseDto fromGame(Game game);
}
