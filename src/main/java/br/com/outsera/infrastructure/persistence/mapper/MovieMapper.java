package br.com.outsera.infrastructure.persistence.mapper;

import java.util.List;

import br.com.outsera.domain.Movie;
import br.com.outsera.infrastructure.csv.MovieCsv;
import br.com.outsera.infrastructure.persistence.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieMapper {

    public MovieEntity toEntity(MovieCsv movieCsv) {
        return new MovieEntity(
                movieCsv.year(),
                movieCsv.title(),
                movieCsv.studios(),
                movieCsv.producers(),
                movieCsv.winner()
        );
    }

    public List<Movie> toMovieList(List<MovieEntity> movieEntityList) {
        return movieEntityList.stream()
                .map(this::toDomain)
                .toList();
    }

    public Movie toDomain(MovieEntity movieEntity) {
        return new Movie(
                movieEntity.getId(),
                movieEntity.getYear(),
                movieEntity.getTitle(),
                movieEntity.getStudios(),
                movieEntity.getProducers(),
                movieEntity.getWinner()
        );
    }

}
