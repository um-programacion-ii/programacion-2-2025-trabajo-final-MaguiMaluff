package um.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final int expMinutes;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.exp-minutes:30}") int expMinutes) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expMinutes = expMinutes;
    }

    public String issueToken(String userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expMinutes * 60 * 1000L);
        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public String validateAndGetSubject(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}