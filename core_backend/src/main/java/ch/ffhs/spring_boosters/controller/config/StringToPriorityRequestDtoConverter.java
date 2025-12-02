package ch.ffhs.spring_boosters.controller.config;

import ch.ffhs.spring_boosters.controller.dto.PriorityRequestDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPriorityRequestDtoConverter implements Converter<String, PriorityRequestDto> {
    @Override
    public PriorityRequestDto convert(String source) {
        return new PriorityRequestDto(source);
    }
}
