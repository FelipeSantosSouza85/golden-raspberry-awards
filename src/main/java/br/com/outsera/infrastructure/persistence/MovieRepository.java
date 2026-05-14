package br.com.outsera.infrastructure.persistence;

import java.util.List;

import br.com.outsera.domain.ProducerWin;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<MovieEntity> {

    public List<ProducerWin> listProducersWins() {
        return getEntityManager()
                .createQuery("""
                        SELECT new br.com.outsera.domain.ProducerWin(p.name, m.year)
                        FROM MovieEntity m
                        JOIN m.producers p
                        WHERE m.winner = true
                        ORDER BY p.name, m.year
                        """, ProducerWin.class)
                .getResultList();
    }
}
