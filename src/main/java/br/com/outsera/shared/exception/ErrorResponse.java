package br.com.outsera.shared.exception;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resposta padrao para erros nao tratados")
public record ErrorResponse(
        @Schema(description = "Data e hora do erro em formato ISO-8601")
        String timestamp,

        @Schema(description = "Codigo HTTP do erro")
        int status,

        @Schema(description = "Frase descritiva do status HTTP")
        String error,

        @Schema(description = "Mensagem amigavel descrevendo o erro")
        String message
) {
}
