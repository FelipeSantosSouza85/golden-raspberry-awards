package br.com.outsera.domain;

import java.util.List;

public record AwardInterval(
    List<ProducerAwardInterval> min,
    List<ProducerAwardInterval> max
) {}
