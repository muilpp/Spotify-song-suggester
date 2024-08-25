package xyz.spotifyrecommender.model.errorhandler;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class ErrorHandlerAccessRevoked implements ResponseErrorHandler {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandlerAccessRevoked.class.getName());

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            LOGGER.info("Usuari s'ha donat de baixa");
        }
    }

    public static class RestUtil {

        private RestUtil() {}
        
        static boolean isError(HttpStatusCode statusCode) {
            return statusCode.is4xxClientError() || statusCode.is5xxServerError();
        }
    }
}