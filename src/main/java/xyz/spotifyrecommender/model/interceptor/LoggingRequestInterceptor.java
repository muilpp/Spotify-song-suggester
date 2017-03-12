package xyz.spotifyrecommender.model.interceptor;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOGGER = Logger.getLogger(LoggingRequestInterceptor.class.getName());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        log(request, body, response);

        return response;
    }

    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        LOGGER.info("Request -> " + request.getURI().toString());

        LOGGER.info("Bearer -> " + request.getHeaders().getValuesAsList("Authorization").toString());
        
        LOGGER.info("Body -> " + new String(body, "UTF-8"));
        LOGGER.info("Response -> " + response.getStatusCode());
    }
}

