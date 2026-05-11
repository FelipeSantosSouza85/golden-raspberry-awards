package br.com.outsera.infrastructure.csv;

import java.io.BufferedReader;
import java.io.IOException;
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

        } catch (IOException ex) {
            throw new CsvLoadException("Falha de I/O ao ler o CSV: " + fileName, ex);
        } catch (IllegalArgumentException ex) {
            throw new CsvLoadException("CSV com formato invalido: " + fileName, ex);
        }
    }

    /**
     * Converte um registro do CSV em um objeto MovieCsv. 
     * O método realiza a conversão dos campos e trata possíveis erros de formatação, como anos inválidos.   
     * @param csvRecord O registro do CSV a ser convertido.
     * @return O objeto MovieCsv correspondente ao registro do CSV.
     */
    private MovieCsv toRecord(CSVRecord csvRecord) {

        try {
            return new MovieCsv(
                    Integer.valueOf(csvRecord.get(HeadersCsv.YEAR.getHeader())),
                    csvRecord.get(HeadersCsv.TITLE.getHeader()),
                    csvRecord.get(HeadersCsv.STUDIOS.getHeader()),
                    csvRecord.get(HeadersCsv.PRODUCERS.getHeader()),
                    isWinner(csvRecord.get(HeadersCsv.WINNER.getHeader())));
        } catch (NumberFormatException ex) {
            throw new CsvLoadException("Ano invalido na linha " + csvRecord.getRecordNumber() + " do CSV " + fileName, ex);
        }
    }

    private boolean isWinner(String value) {
        return "yes".equalsIgnoreCase(value);
    }

}
