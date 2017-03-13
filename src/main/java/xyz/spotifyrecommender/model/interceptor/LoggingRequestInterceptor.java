package xyz.spotifyrecommender.model.interceptor;

import java.io.IOException;
import java.util.logging.Level;
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
        LOGGER.log(Level.INFO, "Request -> [{0}]", request.getURI().toString());
        LOGGER.log(Level.INFO, "Bearer -> [{0}]", request.getHeaders().getValuesAsList("Authorization").toString());
        LOGGER.log(Level.INFO, "Body -> [{0}]", new String(body, "UTF-8"));
        LOGGER.log(Level.INFO, "Response -> [{0}]", response.getStatusCode());
    }
}