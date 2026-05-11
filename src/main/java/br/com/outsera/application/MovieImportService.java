package br.com.outsera.application;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.outsera.infrastructure.csv.MovieCsv;
import br.com.outsera.infrastructure.csv.MovieCsvLoader;
import br.com.outsera.infrastructure.persistence.MovieEntity;
import br.com.outsera.infrastructure.persistence.MovieRepository;
import br.com.outsera.infrastructure.persistence.mapper.MovieMapper;
import br.com.outsera.shared.exception.CsvLoadException;
import br.com.outsera.shared.exception.MovieImportException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MovieImportService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieImportService.class);

    private final MovieCsvLoader movieCsvLoader;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieImportService(MovieCsvLoader movieCsvLoader, MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieCsvLoader = movieCsvLoader;
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    public void importMovies() {

        List<MovieCsv> movies = loadMoviesFromCsv();

        LOG.info("Total de filmes carregados do CSV: {}", movies.size());

        persistMovies(movies);
    }

    private List<MovieCsv> loadMoviesFromCsv() {
        try {
            return movieCsvLoader.loadMovies();
        } catch (CsvLoadException exception) {
            throw new MovieImportException("Nao foi possivel importar a lista de filmes a partir do CSV.", exception);
        }
    }

    @Transactional
    public void persistMovies(List<MovieCsv> movies) {

        List<MovieEntity> movieEntities = movies.stream()
                .map(movieMapper::toEntity)
                .toList();

        LOG.info("Total de filmes a serem persistidos: {}", movieEntities.size());

        movieRepository.persist(movieEntities);
        
        LOG.info("Importacao de filmes concluida com sucesso. Total de filmes persistidos: {}", movieEntities.size());
     }

}
