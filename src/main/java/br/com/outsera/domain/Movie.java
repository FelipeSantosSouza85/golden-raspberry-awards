package br.com.outsera.domain;

public record Movie(
    Long id,
    int year,
    String title,
    String studios,
    String producers,
    boolean winner
) {

}
