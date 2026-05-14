package br.com.outsera.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.outsera.domain.AwardInterval;
import br.com.outsera.domain.ProducerAwardInterval;
import br.com.outsera.domain.ProducerWin;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProducerAwardIntervalService {

   private static final Logger LOG = LoggerFactory.getLogger(ProducerAwardIntervalService.class);

   private final MovieService movieService;

   public ProducerAwardIntervalService(MovieService movieService) {
      this.movieService = movieService;
   }

   /**
    * Calcula os intervalos entre vitorias consecutivas de produtores, e identifica os produtores com o menor e maior intervalo.
     * O intervalo é calculado apenas entre vitorias distintas (anos diferentes) do mesmo produtor.
     * Produtores com menos de duas vitorias distintas nao geram intervalo.
    * @return Um objeto AwardInterval contendo os produtores com o menor e maior intervalo entre vitorias consecutivas.
    */
   public AwardInterval calculateProducerAwardIntervals() {

      // Carrega a lista de vitorias dos produtores, onde cada vitoria representa um par (produtor, ano).
      List<ProducerWin> wins = movieService.listProducersWins();

      LOG.info("Total de vitorias (pares produtor x ano) encontradas: {}", wins.size());

      if (wins.isEmpty()) {
         LOG.info("Nenhuma vitoria encontrada. Encerrando calculo de intervalos de premios.");
         return new AwardInterval(List.of(), List.of());
      }

      List<ProducerAwardInterval> intervals = calculateIntervals(wins);

      if (intervals.isEmpty()) {
         LOG.info("Nenhum produtor com multiplas vitorias encontrado. Encerrando calculo de intervalos de premios.");
         return new AwardInterval(List.of(), List.of());
      }

      // Agrupa os intervalos por valor de intervalo, para facilitar a identificacao dos menores e maiores intervalos.
      Map<Integer, List<ProducerAwardInterval>> intervalsByValue = intervals.stream()
                .collect(Collectors.groupingBy(ProducerAwardInterval::interval));
      
      // Identifica o menor e o maior intervalo entre vitorias consecutivas.
      int minInterval = Collections.min(intervalsByValue.keySet());
      int maxInterval = Collections.max(intervalsByValue.keySet());

      LOG.info("Intervalo minimo: {} | Intervalo maximo: {}", minInterval, maxInterval);

      return new AwardInterval(intervalsByValue.get(minInterval), intervalsByValue.get(maxInterval));
   }

   /**
    * Calcula os intervalos consecutivos entre vitorias de cada produtor.
    * Espera uma lista de produtores vencedores ordenado por (produtor ASC, ano ASC).
    * Produtores com menos de duas vitorias distintas nao geram intervalo.
    */
   private List<ProducerAwardInterval> calculateIntervals(List<ProducerWin> wins) {

      List<ProducerAwardInterval> intervals = new ArrayList<>();
      String currentProducer = null;
      Integer previousWinYear = null;

      // Itera a lista de vitorias, calculando o intervalo entre vitorias consecutivas do mesmo produtor.
      for (ProducerWin win : wins) {

         // Se o produtor da iteração for diferente do produtor atual, inicia o acompanhamento de um novo produtor.
         if (!win.producer().equals(currentProducer)) {
            currentProducer = win.producer();
            previousWinYear = win.year();
            continue;
         }

         // Se o produtor da iteração for o mesmo da iteração anterior, e for o mesmo ano da última vitória registrada,
         // ignora a iteração atual para evitar intervalos zero entre vitórias no mesmo ano.
         if(win.year().equals(previousWinYear)) {
            continue;
         }

         // Se o produtor da iteração for o mesmo da iteração anterior, e for um ano diferente da última vitória registrada,
         // calcula o intervalo entre as vitórias e registra um novo intervalo para o produtor atual.
         intervals.add(new ProducerAwardInterval(
               currentProducer,
               win.year() - previousWinYear,
               previousWinYear,
               win.year()));

         previousWinYear = win.year();
      }

      return intervals;
   }
}
