package ar.edu.um.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties agrupadas bajo prefijo 'proxy' (application.yml / env).
 * Se inyectan en beans que necesitan configuración.
 */
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {

    private final Kafka kafka = new Kafka();
    private final Redis redis = new Redis();
    private final Catedra catedra = new Catedra();
    private final Backend backend = new Backend();
    private final Security security = new Security();

    public Kafka getKafka() { return kafka; }
    public Redis getRedis() { return redis; }
    public Catedra getCatedra() { return catedra; }
    public Backend getBackend() { return backend; }
    public Security getSecurity() { return security; }

    public static class Kafka {
        private String topic = "eventos-actualizacion";
        private String groupId = "alumno-default";
        private boolean enrich = false;
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public boolean isEnrich() { return enrich; }
        public void setEnrich(boolean enrich) { this.enrich = enrich; }
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
        private String username;
        private String password;
        private long refreshIntervalMs = 1800000L;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public long getRefreshIntervalMs() { return refreshIntervalMs; }
        public void setRefreshIntervalMs(long refreshIntervalMs) { this.refreshIntervalMs = refreshIntervalMs; }
    }

    public static class Backend {
        private String baseUrl = "http://localhost:8080";
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }

    public static class Security {
        private boolean disable = false;
        public boolean isDisable() { return disable; }
        public void setDisable(boolean disable) { this.disable = disable; }
    }
}