package br.com.outsera.infrastructure.persistence.mapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.outsera.infrastructure.csv.MovieCsv;
import br.com.outsera.infrastructure.persistence.MovieEntity;
import br.com.outsera.infrastructure.persistence.ProducerEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieMapper {

    public MovieEntity toEntity(MovieCsv movieCsv, Map<String, ProducerEntity> mapProducersByName) {

        Set<ProducerEntity> producers = movieCsv.producers().stream()
                .map(mapProducersByName::get)
                .collect(Collectors.toCollection(HashSet::new));

        return new MovieEntity(
                movieCsv.year(),
                movieCsv.title(),
                movieCsv.studios(),
                producers,
                movieCsv.winner()
        );
    }

}
