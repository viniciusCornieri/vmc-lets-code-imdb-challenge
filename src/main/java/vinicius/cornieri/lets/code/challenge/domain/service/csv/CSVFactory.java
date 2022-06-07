package vinicius.cornieri.lets.code.challenge.domain.service.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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

    public static Reader buildReader(String path) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        return new BufferedReader(new InputStreamReader(is));
    }
}
