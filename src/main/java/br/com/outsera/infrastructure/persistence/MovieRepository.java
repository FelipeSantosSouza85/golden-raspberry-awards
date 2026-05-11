package br.com.outsera.infrastructure.persistence;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<MovieEntity> {

    public List<MovieEntity> listWinners() {
        return list("winner", true);
    }
}
