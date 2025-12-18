package um.backend.auth;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import um.backend.security.JwtService;
import um.backend.security.SessionEntity;
import um.backend.security.SessionService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final SessionService sessions;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt, SessionService sessions) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.sessions = sessions;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> signup(@RequestBody Map<String, String> body) {
        return Mono.fromCallable(() -> {
                    String username = body.get("username");
                    String password = body.get("password");
                    String userId = body.getOrDefault("userId", username);
                    if (username == null || password == null) {
                        return ResponseEntity.badRequest().body(Map.<String,Object>of("error", "missing credentials"));
                    }
                    if (users.findByUsername(username).isPresent()) {
                        return ResponseEntity.status(409).body(Map.<String,Object>of("error", "user exists"));
                    }
                    UserEntity u = new UserEntity();
                    u.setUsername(username);
                    u.setPasswordHash(encoder.encode(password));
                    u.setUserIdRef(userId);
                    users.save(u);
                    return ResponseEntity.ok(Map.<String,Object>of("created", true));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestBody Map<String, String> body) {
        return Mono.fromCallable(() -> {
                    String username = body.get("username");
                    String password = body.get("password");
                    var u = users.findByUsername(username).orElse(null);
                    if (u == null || !encoder.matches(password, u.getPasswordHash())) {
                        return ResponseEntity.status(401).body(Map.<String,Object>of("error", "invalid credentials"));
                    }
                    SessionEntity s = sessions.create(u.getUserIdRef());
                    String token = jwt.issueToken(u.getUserIdRef());
                    return ResponseEntity.ok(Map.<String,Object>of(
                            "id_token", token,
                            "sessionId", s.getId().toString(),
                            "userId", u.getUserIdRef()
                    ));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, Object>>> logout(@RequestHeader("Authorization") String auth) {
        return Mono.fromCallable(() -> {
                    String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : null;
                    if (token == null) {
                        return ResponseEntity.status(401).body(Map.<String,Object>of("error", "missing token"));
                    }
                    String userId = jwt.validateAndGetSubject(token);
                    sessions.logoutAll(userId);
                    return ResponseEntity.ok(Map.<String,Object>of("logout", true));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}