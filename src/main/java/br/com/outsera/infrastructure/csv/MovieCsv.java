package br.com.outsera.infrastructure.csv;

public record MovieCsv(
    Integer year,
        String title,
        String studios,
        String producers,
        boolean winner
) {}
