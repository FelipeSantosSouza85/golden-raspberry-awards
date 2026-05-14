package br.com.outsera.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.outsera.domain.ProducerWin;
import br.com.outsera.infrastructure.csv.MovieCsv;
import br.com.outsera.infrastructure.csv.MovieCsvLoader;
import br.com.outsera.infrastructure.persistence.MovieEntity;
import br.com.outsera.infrastructure.persistence.MovieRepository;
import br.com.outsera.infrastructure.persistence.ProducerEntity;
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

    @Transactional
    public void importMovies() {

        List<MovieCsv> movies = loadMoviesFromCsv();

        LOG.info("Total de filmes carregados do CSV: {}", movies.size());

        Map<String, ProducerEntity> mapProducersByName = createMapProducers(movies);

        LOG.info("Total de produtores unicos: {}", mapProducersByName.size());

        persistMovies(movies, mapProducersByName);
    }

    private List<MovieCsv> loadMoviesFromCsv() {
        try {
            return movieCsvLoader.loadMovies();
        } catch (CsvLoadException exception) {
            throw new MovieImportException("Nao foi possivel importar a lista de filmes a partir do CSV.", exception);
        }
    }

    /**
     * Cria um mapa de produtores a partir da lista de filmes,
     * onde a chave é o nome do produtor e o valor é a entidade do produtor.
     * Utiliza computeIfAbsent para garantir que cada produtor seja criado apenas uma vez, mesmo que apareça em varios filmes.
     */
    private Map<String, ProducerEntity> createMapProducers(List<MovieCsv> movies) {

        Map<String, ProducerEntity> producersByName = new HashMap<>();
        for (MovieCsv movie : movies) {
            for (String name : movie.producers()) {
                producersByName.computeIfAbsent(name, ProducerEntity::new);
            }
        }
        return producersByName;
    }

    /**
     * Persiste a lista de filmes no banco de dados, associando os produtores correspondentes a partir do mapa criado anteriormente.
     * @param movies A lista de filmes a ser persistida, carregada do CSV.
     * @param mapProducersByName O mapa de produtores, onde a chave é o nome do produtor e o valor é a entidade do produtor correspondente.
     */
    private void persistMovies(List<MovieCsv> movies, Map<String, ProducerEntity> mapProducersByName) {

        //Cria uma lista de entidades de filme a partir da lista de filmes do CSV,
        // utilizando o mapper para converter cada filme e associar os produtores correspondentes a partir do mapa criado anteriormente.
        List<MovieEntity> movieEntities = movies.stream()
                .map(movie -> movieMapper.toEntity(movie, mapProducersByName))
                .toList();

        LOG.info("Total de filmes a serem persistidos: {}", movieEntities.size());

        movieRepository.persist(movieEntities);

        LOG.info("Importacao de filmes concluida com sucesso. Total de filmes persistidos: {}", movieEntities.size());
    }

    public List<ProducerWin> listProducersWins() {
        return movieRepository.listProducersWins();
    }

}
