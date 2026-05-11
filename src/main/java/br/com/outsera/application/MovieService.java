package br.com.outsera.application;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.outsera.domain.Movie;
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
public class MovieService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    private final MovieCsvLoader movieCsvLoader;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieService(
        MovieCsvLoader movieCsvLoader,
        MovieRepository movieRepository,
        MovieMapper movieMapper
    ) {
        this.movieCsvLoader = movieCsvLoader;
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    /**
     * Importa os filmes a partir do arquivo CSV e os persiste no banco de dados.
     * O processo é realizado dentro de uma transação para garantir a integridade dos dados.
     */
    @Transactional
    public void importMovies() {

        List<MovieCsv> movies = loadMoviesFromCsv();

        LOG.info("Total de filmes carregados do CSV: {}", movies.size());

        persistMovies(movies);
    }

    /**
     * Carrega a lista de filmes a partir do arquivo CSV utilizando o MovieCsvLoader.
     * Em caso de falha na leitura do arquivo, uma MovieImportException é lançada.
     * @return A lista de filmes carregados do CSV.
     */
    private List<MovieCsv> loadMoviesFromCsv() {
        try {
            return movieCsvLoader.loadMovies();
        } catch (CsvLoadException exception) {
            throw new MovieImportException("Nao foi possivel importar a lista de filmes a partir do CSV.", exception);
        }
    }

    /**
     * Persiste a lista de filmes no banco de dados.
     * @param movies A lista de filmes a serem persistidos.
     */
    private void persistMovies(List<MovieCsv> movies) {

        List<MovieEntity> movieEntities = movies.stream()
                .map(movieMapper::toEntity)
                .toList();

        LOG.info("Total de filmes a serem persistidos: {}", movieEntities.size());

        movieRepository.persist(movieEntities);

        LOG.info("Importacao de filmes concluida com sucesso. Total de filmes persistidos: {}", movieEntities.size());
     }

     /**
      * Recupera a lista de filmes vencedores do banco de dados e os mapeia para o modelo de domínio Movie.
      * @return A lista de filmes vencedores mapeados para o modelo de domínio Movie.
      */
     public List<Movie> listWinners() {
        return movieMapper.toMovieList(movieRepository.listWinners());
     }

}
