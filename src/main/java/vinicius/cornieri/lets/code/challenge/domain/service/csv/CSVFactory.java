package vinicius.cornieri.lets.code.challenge.domain.service.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVFactory {

    public static final char CSV_SEPARATOR = ';';

    private CSVFactory() {
        throw new IllegalStateException("Static utility class should not be instantiated");
    }

    @SneakyThrows
    public static <T> CsvToBean<T> buildCSVToBean(String csvPath, Class<T> clazz) {
        return new CsvToBeanBuilder<T>(buildReader(csvPath))
            .withType(clazz)
            .withSeparator(CSV_SEPARATOR)
            .build();
    }

    public static Reader buildReader(String path) throws IOException, URISyntaxException {
        return Files.newBufferedReader(buildPath(path));
    }

    public static Path buildPath(String path) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(path).toURI());
    }

}
