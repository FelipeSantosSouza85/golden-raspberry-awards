package br.com.outsera.infrastructure.persistence.mapper;

import br.com.outsera.infrastructure.csv.MovieCsv;
import br.com.outsera.infrastructure.persistence.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieMapper {

    public MovieEntity toEntity(MovieCsv record) {
        return new MovieEntity(
                record.year(),
                record.title(),
                record.studios(),
                record.producers(),
                record.winner()
        );
    }
}
