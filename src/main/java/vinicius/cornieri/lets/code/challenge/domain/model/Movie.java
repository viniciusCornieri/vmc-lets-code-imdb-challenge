package vinicius.cornieri.lets.code.challenge.domain.model;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvIgnore
    @Column(name = "ID", nullable = false)
    private Long id;

    @CsvBindByPosition(position = 0, required = true)
    @Column(name = "IMDB_ID", nullable = false)
    private String imdbId;

    @CsvBindByPosition(position = 1, required = true)
    @Column(name = "TITLE", nullable = false)
    private String title;

    @CsvBindByPosition(position = 2)
    @Column(name = "GENRES")
    private String genres;

    @CsvBindByPosition(position = 3, required = true)
    @Column(name = "RATING")
    private int rating;

    @CsvBindByPosition(position = 4)
    @Column(name = "RELEASE_YEAR")
    private int year;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Movie movie = (Movie) o;

        return Objects.equals(imdbId, movie.imdbId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(imdbId);
    }
}
