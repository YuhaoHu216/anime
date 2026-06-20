package top.huyuhao.anime.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 — token 生成、解析、校验
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:animeBackendSecretKeyForJwtToken2024!@#}")
    private String secret;

    @Value("${jwt.expiration:36000}") // 默认 10 小时
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // HMAC-SHA 密钥至少需要 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT token
     */
    public String generateToken(Integer userId, String username) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);

        return Jwts.builder()
                .claim("userId", userId)
                .subject(username)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 token 中解析 Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
            throw new RuntimeException("无效的认证令牌", e);
        }
    }

    /**
     * 从 token 中提取 userId
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object rawUserId = claims.get("userId");
        if (rawUserId instanceof Number) {
            return ((Number) rawUserId).intValue();
        }
        return null;
    }

    /**
     * 从 token 中提取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 校验 token 是否有效（未过期且签名正确）
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (RuntimeException e) {
            return false;
        }
    }
}
