package vinicius.cornieri.lets.code.challenge.domain.service.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
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

    private static CSVParser buildTSVParser() {
        return new CSVParserBuilder()
            .withSeparator('\t')
            .withIgnoreQuotations(true)
            .build();
    }

    public static CSVReader buildTSVReader(String csvPath) {
        CSVParser tsvParser = buildTSVParser();

        return buildCSVReader(csvPath, tsvParser);
    }

    @SneakyThrows
    public static <T> CsvToBean<T> buildCSVToBean(String csvPath, Class<T> clazz) {
        return new CsvToBeanBuilder<T>(buildReader(csvPath))
            .withType(clazz)
            .withSeparator(CSV_SEPARATOR)
            .build();
    }

    @SneakyThrows
    private static CSVReader buildCSVReader(String csvPath, CSVParser parser) {
        Reader reader = buildReader(csvPath);

        return new CSVReaderBuilder(reader)
            .withSkipLines(1)
            .withCSVParser(parser)
            .build();
    }

    private static Reader buildReader(String path) throws IOException, URISyntaxException {
        return Files.newBufferedReader(buildPath(path));
    }

    public static Path buildPath(String path) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(path).toURI());
    }

}
