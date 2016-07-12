package xyz.spotifyrecommender.model;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class BearerHeaderInterceptor implements ClientHttpRequestInterceptor{
    private final String bearer;

    public BearerHeaderInterceptor(String bearer) {
        this.bearer = bearer;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add("Authorization", "Bearer " + bearer);
        return execution.execute(request, body);
    }
}