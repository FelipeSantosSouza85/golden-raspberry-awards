package br.com.outsera.api.mapper;

import br.com.outsera.api.dto.ProducerIntervalResponse;
import br.com.outsera.domain.ProducerAwardInterval;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProducerAwardIntervalMapper {

    public ProducerIntervalResponse toResponse(ProducerAwardInterval interval) {
        return new ProducerIntervalResponse(
                interval.producer(),
                interval.interval(),
                interval.previousWin(),
                interval.followingWin()
        );
    }
}
