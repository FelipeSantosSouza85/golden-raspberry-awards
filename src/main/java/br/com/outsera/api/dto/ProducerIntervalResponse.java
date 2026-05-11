package br.com.outsera.api.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Intervalo entre duas vitorias consecutivas de um produtor")
public record ProducerIntervalResponse(

    @Schema(description = "Nome do produtor", examples = "Joel Silver")
    String producer,

    @Schema(description = "Intervalo em anos entre as vitorias", examples = "1")
    int interval,

    @Schema(description = "Ano da vitoria anterior", examples = "1990")
    int previousWin,

    @Schema(description = "Ano da vitoria seguinte", examples = "1991")
    int followingWin
) {

}
