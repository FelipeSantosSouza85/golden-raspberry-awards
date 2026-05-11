package br.com.outsera.api.mapper;

import java.util.List;

import br.com.outsera.api.dto.AwardIntervalResponse;
import br.com.outsera.api.dto.ProducerIntervalResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AwardIntervalMapper {

    public AwardIntervalResponse toResponse(
        List<ProducerIntervalResponse> min, 
        List<ProducerIntervalResponse> max
    ) {
        return new AwardIntervalResponse(min, max);
    }
}
