package br.com.outsera.infrastructure.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.outsera.shared.exception.CsvLoadException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieCsvLoader {


    @ConfigProperty(name = "app.csv.file-name", defaultValue = "Movielist.csv")
    String fileName;

    public List<MovieCsv> loadMovies() {
        
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new CsvLoadException("Arquivo CSV nao encontrado no classpath: " + fileName);
        }

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                );

                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .get()
                        .parse(reader)
        ) {
            return parser.stream()
                    .map(this::toRecord)
                    .toList();

        } catch (Exception exception) {
            throw new CsvLoadException("Falha ao ler o arquivo CSV: " + fileName, exception);
        }
    }

    private MovieCsv toRecord(CSVRecord csvRecord) {
        return new MovieCsv(
                Integer.valueOf(csvRecord.get(HeadersCsv.YEAR.getHeader())),
                csvRecord.get(HeadersCsv.TITLE.getHeader()),
                csvRecord.get(HeadersCsv.STUDIOS.getHeader()),
                csvRecord.get(HeadersCsv.PRODUCERS.getHeader()),
                isWinner(csvRecord.get(HeadersCsv.WINNER.getHeader()))
        );
    }

    private boolean isWinner(String value) {
        return "yes".equalsIgnoreCase(value);
    }

}
