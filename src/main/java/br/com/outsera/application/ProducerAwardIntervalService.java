package br.com.outsera.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.outsera.api.dto.AwardIntervalResponse;
import br.com.outsera.api.dto.ProducerIntervalResponse;
import br.com.outsera.api.mapper.AwardIntervalMapper;
import br.com.outsera.api.mapper.ProducerAwardIntervalMapper;
import br.com.outsera.domain.ProducerAwardInterval;
import br.com.outsera.infrastructure.persistence.MovieEntity;
import br.com.outsera.infrastructure.persistence.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProducerAwardIntervalService {

   private static final Logger LOG = LoggerFactory.getLogger(ProducerAwardIntervalService.class);

   private static final String AND_REGEX = "(?i)\\s+and\\s+";

   private final MovieRepository movieRepository;
   private final ProducerAwardIntervalMapper producerAwardIntervalMapper;
   private final AwardIntervalMapper awardIntervalMapper;

   public ProducerAwardIntervalService(
      MovieRepository movieRepository, 
      ProducerAwardIntervalMapper producerAwardIntervalMapper,
      AwardIntervalMapper awardIntervalMapper
   ) {
      this.movieRepository = movieRepository;
      this.producerAwardIntervalMapper = producerAwardIntervalMapper;
      this.awardIntervalMapper = awardIntervalMapper;
   }


   public AwardIntervalResponse calculateProducerAwardIntervals() {

      //Recupera os filmes vencedores do banco de dados.
      List<MovieEntity> winners = movieRepository.listWinners();

      LOG.info("Total de filmes vencedores encontrados: {}", winners.size());

      if(winners.isEmpty()) {
         LOG.info("Nenhum filme vencedor encontrado. Encerrando cálculo de intervalos de prêmios.");
         return new AwardIntervalResponse(List.of(), List.of());
      }

      //Cria o mapa de produtores e seus anos de vitória.
      Map<String, List<Integer>> producerWins = groupWinsByProducer(winners);

      LOG.info("Total de produtores vencedores encontrados: {}", producerWins.size());

      //Calcula os intervalos de prêmios para cada produtor.
      List<ProducerAwardInterval> intervals = calculateIntervals(producerWins);

      if(intervals.isEmpty()) {
         LOG.info("Nenhum produtor com múltiplas vitórias encontrado. Encerrando cálculo de intervalos de prêmios.");
         return new AwardIntervalResponse(List.of(), List.of());
      }

      //Calcula o intervalo mínimo e máximo entre as vitórias dos produtores.
      int minInterval = intervals.stream()
            .mapToInt(ProducerAwardInterval::interval)
            .min()
            .orElseThrow(() -> new IllegalStateException("Nao foi possivel calcular o intervalo minimo."));

      LOG.info("Intervalo mínimo entre prêmios encontrado: {}", minInterval);

      //Calcula o intervalo máximo entre as vitórias dos produtores
      int maxInterval = intervals.stream()
            .mapToInt(ProducerAwardInterval::interval)
            .max()
            .orElseThrow(() -> new IllegalStateException("Nao foi possivel calcular o intervalo maximo."));

      LOG.info("Intervalo máximo entre prêmios encontrado: {}", maxInterval);

      //Filtra os produtores que possuem o intervalo máximo e mínimo calculados.
      List<ProducerIntervalResponse> filteredMaxIntervals = filterIntervalsByInterval(intervals, maxInterval);
      List<ProducerIntervalResponse> filteredMinIntervals = filterIntervalsByInterval(intervals, minInterval);

      return awardIntervalMapper.toResponse(filteredMinIntervals, filteredMaxIntervals);
   }


   /**
    * Agrupa os anos de vitória dos produtores a partir da lista de filmes vencedores, 
    * criando um mapa onde a chave é o nome do produtor e o valor é uma lista de anos em que ele ganhou o prêmio.
    * @param winners lista de filmes vencedores, onde cada filme contém o nome dos produtores e o ano em que ganhou o prêmio.
    * @return mapa onde a chave é o nome do produtor e o valor é uma lista de anos em que ele ganhou o prêmio.
    */
   private Map<String, List<Integer>> groupWinsByProducer(List<MovieEntity> winners) {
      
      Map<String, List<Integer>> winsByProducer = new HashMap<>();

      for (MovieEntity winner : winners) {

         //Cria uma lista com o nome dos produtores, tratando os casos de múltiplos produtores separados por "and" ou vírgula.
         List<String> producers = splitProducers(winner.producers);

         //Itera a lista de produtores criada e cria um mapa onde a chave é o nome do produtor e o valor é uma lista de anos em que ele ganhou o prêmio.
         producers.forEach(producer -> {
               winsByProducer.computeIfAbsent(producer, k -> new ArrayList<>()).add(winner.year);
            });
      }

      return winsByProducer;
   }

   /**
    * Calcula os intervalos de prêmios para cada produtor a partir do mapa de produtores e seus anos de vitória.
    * @param winsByProducer mapa onde a chave é o nome do produtor e o valor é uma lista de anos em que ele ganhou o prêmio.
    * @return lista de objetos de domínio contendo os dados do intervalo de prêmios para cada produtor.
    */
   private List<ProducerAwardInterval> calculateIntervals(Map<String, List<Integer>> winsByProducer) {
   
      List<ProducerAwardInterval> intervals = new ArrayList<>();

      for (Map.Entry<String, List<Integer>> entry : winsByProducer.entrySet()) {

         //Recupera o produtor e ordena os anos de vitória em ordem crescente.
         String producer = entry.getKey();
         List<Integer> winYears = entry.getValue().stream()
                  .sorted()
                  .toList();

         //Caso o produtor não tenha 2 vitórias é ignorado.
         if(winYears.size() < 2) {
            continue;
         }

         //Itera sobre os anos de vitória, começando o contador no segundo elemento, para calcular o intervalo entre as vitórias.
         for (int index = 1; index < winYears.size(); index++) {

            //Recupera o ano da vitória anterior e o da seguinte e calcula o intervalo entre elas.
            int previousWin = winYears.get(index - 1);
            int followingWin = winYears.get(index);
            int interval = followingWin - previousWin;

            //Cria um objeto de domínio com o os dados do intervalo e adiciona na lista de intervalos.
            intervals.add(new ProducerAwardInterval(producer, interval, previousWin, followingWin));
         }
      }
   
      return intervals;
   }

   /**
    * Cria uma lista com o nome dos produtores a partir de uma string,
    * tratando os casos de múltiplos produtores separados por "and" ou vírgula.
    * @param producers string contendo o nome dos produtores, podendo conter múltiplos produtores separados por "and" ou vírgula.
    * @return lista de strings contendo o nome dos produtores, sem espaços em branco no início ou no fim, e sem elementos em branco.
    */
   private List<String> splitProducers(String producers) {
      return Arrays.stream(producers.replaceAll(AND_REGEX, ",").split(","))
               .map(String::trim)
               .filter(name -> !name.isBlank())
               .toList();
   }


   /**
    * Filtra os intervalos de prêmios por um intervalo específico.
    * @param intervals lista de intervalos de prêmios.
    * @param targetInterval intervalo alvo para filtragem.
    * @return lista de respostas contendo os intervalos filtrados.
    */
   private List<ProducerIntervalResponse> filterIntervalsByInterval(List<ProducerAwardInterval> intervals, int targetInterval) {
      return intervals.stream()
            .filter(interval -> interval.interval() == targetInterval)
            .map(producerAwardIntervalMapper::toResponse)
            .toList();
   }
}
