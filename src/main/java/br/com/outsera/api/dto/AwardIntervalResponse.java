package br.com.outsera.api.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resposta contendo os produtores com menor e maior intervalo entre vitorias")
public record AwardIntervalResponse(

    @Schema(description = "Produtores com menor intervalo entre duas vitorias consecutivas")
    List<ProducerIntervalResponse> min,

    @Schema(description = "Produtores com maior intervalo entre duas vitorias consecutivas")
    List<ProducerIntervalResponse> max
) {

}
