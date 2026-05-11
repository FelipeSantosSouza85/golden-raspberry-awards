package br.com.outsera.shared.exception;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    private static final String GENERIC_MESSAGE = "Ocorreu um erro inesperado ao processar a requisicao.";

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof WebApplicationException webApplicationException) {
            return webApplicationException.getResponse();
        }

        LOG.error("Erro nao tratado durante o processamento da requisicao", exception);

        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                GENERIC_MESSAGE
        );

        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
