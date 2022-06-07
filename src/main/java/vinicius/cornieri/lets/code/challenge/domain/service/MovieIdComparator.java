package vinicius.cornieri.lets.code.challenge.domain.service;

import vinicius.cornieri.lets.code.challenge.domain.model.Movie;

public class MovieIdComparator {

    private MovieIdComparator(){}

    public static Movie lowerIdMovie(Movie a, Movie b) {
        return a.getId() < b.getId() ? a : b;
    }

    public static Movie higherIdMovie(Movie a, Movie b) {
        return a.getId() > b.getId() ? a : b;
    }

}
