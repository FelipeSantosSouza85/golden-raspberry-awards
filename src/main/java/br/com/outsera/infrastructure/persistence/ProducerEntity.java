package br.com.outsera.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producers")
public class ProducerEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String name;

    public ProducerEntity() {
    }

    public ProducerEntity(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ProducerEntity other))
            return false;
        return name != null && name.equals(other.name);
    }

    @Override
    public String toString() {
        return "ProducerEntity [id=" + id + ", name=" + name + "]";
    }

}
