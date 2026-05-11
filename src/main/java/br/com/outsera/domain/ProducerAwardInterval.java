package br.com.outsera.domain;

public record ProducerAwardInterval(
    String producer,
    int interval,
    int previousWin,
    int followingWin
) {

}
