package br.com.outsera.infrastructure.persistence;

import java.util.HashSet;
import java.util.Set;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @Column(nullable = false)
    public Boolean winner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "movie_producers",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "producer_id")
    )
    public Set<ProducerEntity> producers = new HashSet<>();

    public MovieEntity() {
    }

    public MovieEntity(Integer year, String title, String studios, Set<ProducerEntity> producers, Boolean winner) {
        this.year = year;
        this.title = title;
        this.studios = studios;
        this.producers = producers;
        this.winner = winner;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof MovieEntity other))
            return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return "MovieEntity [id=" + id + ", year=" + year + ", title=" + title + ", studios=" + studios + ", winner="
                + winner + "]";
    }

}
