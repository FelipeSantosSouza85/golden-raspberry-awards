package br.com.outsera.api;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.outsera.api.dto.AwardIntervalResponse;
import br.com.outsera.api.mapper.AwardIntervalMapper;
import br.com.outsera.application.ProducerAwardIntervalService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("v1/producers/award-intervals")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Producers", description = "Consultas relacionadas aos produtores vencedores do Golden Raspberry Awards")
public class ProducerAwardIntervalResource {

        private static final Logger LOG = LoggerFactory.getLogger(ProducerAwardIntervalResource.class);

        private final ProducerAwardIntervalService producerAwardIntervalService;
        private final AwardIntervalMapper awardIntervalMapper;

        public ProducerAwardIntervalResource(
                ProducerAwardIntervalService producerAwardIntervalService,
                AwardIntervalMapper awardIntervalMapper
        ) {
                this.producerAwardIntervalService = producerAwardIntervalService;
                this.awardIntervalMapper = awardIntervalMapper;
        }

        @GET
        @Operation(summary = "Consulta os produtores com maior e menor intervalo entre premios",
         description = "Retorna os produtores com o menor e o maior intervalo entre duas vitorias consecutivas no Golden Raspberry Awards.")
        @APIResponse(responseCode = "200", description = "Intervalos calculados com sucesso",
         content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AwardIntervalResponse.class))
        )
        public AwardIntervalResponse getProducerAwardIntervals() {
                LOG.info("Iniciando consulta de intervalos de premios dos produtores");
                return awardIntervalMapper.toResponse(producerAwardIntervalService.calculateProducerAwardIntervals());
        }
}
