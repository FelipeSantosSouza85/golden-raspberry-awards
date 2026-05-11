package br.com.outsera.domain;

public record Movie(
    int year,
    String title,
    String studios,
    String producers,
    boolean winner
) {

}
