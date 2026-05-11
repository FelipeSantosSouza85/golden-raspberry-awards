package br.com.outsera.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "movies")
public class MovieEntity extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "movie_year", nullable = false)
    public Integer year;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public String studios;

    @Column(nullable = false, length = 1000)
    public String producers;

    @Column(nullable = false)
    public Boolean winner;

    public MovieEntity() {
    }

    public MovieEntity(Integer year, String title, String studios, String producers, Boolean winner) {
        this.year = year;
        this.title = title;
        this.studios = studios;
        this.producers = producers;
        this.winner = winner;
    }

    public Long getId() {
        return id;
    }

    public Integer getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String getStudios() {
        return studios;
    }

    public String getProducers() {
        return producers;
    }

    public Boolean getWinner() {
        return winner;
    }

}
