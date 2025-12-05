package ar.edu.um.proxy.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;

/*
 * RestExceptionHandler
 *
 * - Maneja errores comunes y devuelve JSON consistente.
 * - Handler para WebClientResponseException que preserva el status del upstream.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(),
                "Validation Error", msg, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ProxyNotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(ProxyNotFoundException ex, HttpServletRequest req) {
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.NOT_FOUND.value(),
                "Not Found", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ProxyException.class)
    public ResponseEntity<ErrorBody> handleProxy(ProxyException ex, HttpServletRequest req) {
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(),
                "Proxy Error", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // NUEVO: Propaga el status original del upstream (4xx/5xx) devuelto por RestClient/WebClient
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorBody> handleWebClientResponse(WebClientResponseException ex, HttpServletRequest req) {
        HttpStatus upstreamStatus = (HttpStatus) ex.getStatusCode();

        String message = ex.getResponseBodyAsString(); // Cuerpo textual del upstream, si existe
        ErrorBody body = new ErrorBody(
                Instant.now().toString(),
                upstreamStatus.value(),
                upstreamStatus.getReasonPhrase(),
                (message == null || message.isBlank()) ? ex.getMessage() : message,
                req.getRequestURI()
        );
        return ResponseEntity.status(upstreamStatus).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        ErrorBody body = new ErrorBody(Instant.now().toString(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Error", "Ocurrió un error interno en el proxy", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    public static class ErrorBody {
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        public ErrorBody() {}
        public ErrorBody(String timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
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