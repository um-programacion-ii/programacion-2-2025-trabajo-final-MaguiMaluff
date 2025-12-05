package ar.edu.um.proxy.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * ProxyProperties
 *
 * - Agrupa propiedades bajo el prefijo "proxy" leídas de application.yml/env.
 * - kafka, redis, catedra, security, backend (baseUrl) para que el proxy sepa a dónde notificar.
 *
 * Uso:
 * - properties.getBackend().getBaseUrl() devuelve la URL base (p. ej., http://localhost:8080).
 * - Se puede sobreescribir con la variable PROXY_BACKEND_BASE_URL.
 */
@Getter
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {

    private final Kafka kafka = new Kafka();
    private final Redis redis = new Redis();
    private final Catedra catedra = new Catedra();
    private final Backend backend = new Backend(); // NUEVO
    private Security security = new Security();

    public Kafka getKafka() { return kafka; }
    public Redis getRedis() { return redis; }
    public Catedra getCatedra() { return catedra; }
    public Backend getBackend() { return backend; } // NUEVO
    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }

    public static class Kafka {
        private String topic = "eventos-actualizacion";
        private String groupId = "alumno-default";
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
    }

    public static class Redis {
        private String host = "192.168.194.250";
        private int port = 6379;
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
    }

    public static class Catedra {
        private String baseUrl = "http://192.168.194.250:8080";
        private String token;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class Backend {
        private String baseUrl = "http://localhost:8080";
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }

    public static class Security {
        private boolean disable = false;
        private String jwkSetUri;
        private String publicKey;
        private String sharedSecret;

        public boolean isDisable() { return disable; }
        public void setDisable(boolean disable) { this.disable = disable; }
        public String getJwkSetUri() { return jwkSetUri; }
        public void setJwkSetUri(String jwkSetUri) { this.jwkSetUri = jwkSetUri; }
        public String getPublicKey() { return publicKey; }
        public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
        public String getSharedSecret() { return sharedSecret; }
        public void setSharedSecret(String sharedSecret) { this.sharedSecret = sharedSecret; }
    }
}