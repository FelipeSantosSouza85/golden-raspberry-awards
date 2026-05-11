package br.com.outsera.infrastructure.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.outsera.application.MovieImportService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class MovieInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(MovieInitializer.class);

    private final MovieImportService movieImportService;

    public MovieInitializer(MovieImportService movieImportService) {
        this.movieImportService = movieImportService;
    }

    /**
     * Observa o evento de startup da aplicação e inicia o processo de importação dos filmes a partir do CSV.
     * @param event O evento de startup disparado pelo Quarkus.
     */
    void onStart(@Observes StartupEvent event) {
        LOG.info("Iniciando processo de importacao de filmes a partir do CSV.");
        movieImportService.importMovies();
    }
}
