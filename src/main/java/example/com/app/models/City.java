package example.com.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class City {
    @JsonAlias({"id"})
    @Setter(AccessLevel.PRIVATE)
    int id;
    @JsonAlias({"name"})
    @Setter(AccessLevel.PRIVATE)
    String name;
    @JsonAlias({"population"})
    @Setter(AccessLevel.PRIVATE)
    int population;

    // Jackson needs the default constructor
    public City() {}

    public City(String name, int population) {
        setName(name);
        setPopulation(population);
    }
}
