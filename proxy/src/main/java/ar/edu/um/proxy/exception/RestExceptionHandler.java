package ar.edu.um.proxy.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Handler global de excepciones para devolver JSON consistente.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex, ServerWebExchange exchange) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining(", "));
        String path = extractPath(exchange);
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(),
                "Validation Error", msg, path);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ProxyNotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(ProxyNotFoundException ex, ServerWebExchange exchange) {
        String path = extractPath(exchange);
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.NOT_FOUND.value(),
                "Not Found", ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ProxyException.class)
    public ResponseEntity<ErrorBody> handleProxy(ProxyException ex, ServerWebExchange exchange) {
        String path = extractPath(exchange);
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(),
                "Proxy Error", ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex, ServerWebExchange exchange) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        String path = extractPath(exchange);
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Error", "Ocurrió un error interno en el proxy", path);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String extractPath(ServerWebExchange exchange) {
        if (exchange == null || exchange.getRequest() == null || exchange.getRequest().getPath() == null) {
            return "";
        }
        return exchange.getRequest().getPath().value();
    }

    public static class ErrorBody {
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        public ErrorBody() {}
        public ErrorBody(String timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp; this.status = status; this.error = error; this.message = message; this.path = path;
        }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }
}