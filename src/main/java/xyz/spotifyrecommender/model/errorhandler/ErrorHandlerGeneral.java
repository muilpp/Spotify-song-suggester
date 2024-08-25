package xyz.spotifyrecommender.model.errorhandler;

import java.io.IOException;
import org.jboss.logging.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class ErrorHandlerGeneral implements ResponseErrorHandler {

  private static final Logger LOGGER = Logger.getLogger(ErrorHandlerGeneral.class.getName());

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return RestUtil.isError(response.getStatusCode());
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    LOGGER.info("Failed : HTTP error code -> " + response.getStatusCode().value());
    LOGGER.info(response.getStatusText());
  }

  public static class RestUtil {

    private RestUtil() {
    }

    public static boolean isError(HttpStatusCode statusCode) {
      return statusCode.is4xxClientError() || statusCode.is5xxServerError();
    }
  }
}