package br.com.outsera.api.mapper;

import java.util.List;

import br.com.outsera.api.dto.AwardIntervalResponse;
import br.com.outsera.api.dto.ProducerIntervalResponse;
import br.com.outsera.domain.AwardInterval;
import br.com.outsera.domain.ProducerAwardInterval;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AwardIntervalMapper {

    private final ProducerAwardIntervalMapper producerAwardIntervalMapper;

    public AwardIntervalMapper(ProducerAwardIntervalMapper producerAwardIntervalMapper) {
        this.producerAwardIntervalMapper = producerAwardIntervalMapper;
    }

    public AwardIntervalResponse toResponse(AwardInterval awardInterval) {
        return new AwardIntervalResponse(
                toProducerIntervals(awardInterval.min()),
                toProducerIntervals(awardInterval.max()));
    }

    private List<ProducerIntervalResponse> toProducerIntervals(List<ProducerAwardInterval> intervals) {
        return intervals.stream()
                .map(producerAwardIntervalMapper::toResponse)
                .toList();
    }
}
