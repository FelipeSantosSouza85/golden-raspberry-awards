package br.com.outsera.infrastructure.csv;

import java.util.Set;

public record MovieCsv(
    Integer year,
    String title,
    String studios,
    Set<String> producers,
    boolean winner
) {}
