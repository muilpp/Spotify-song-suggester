package xyz.spotifyrecommender.model.error_handler;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class ErrorHandlerGeneral implements ResponseErrorHandler {
	private final static Logger LOGGER = Logger.getLogger(ErrorHandlerGeneral.class.getName());

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return RestUtil.isError(response.getStatusCode());
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
        LOGGER.info("Failed : HTTP error code -> " + response.getStatusCode().value());
        LOGGER.info(response.getStatusCode().getReasonPhrase());
	}

	public static class RestUtil {

		public static boolean isError(HttpStatus status) {
			HttpStatus.Series series = status.series();
			return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
		}
	}
}