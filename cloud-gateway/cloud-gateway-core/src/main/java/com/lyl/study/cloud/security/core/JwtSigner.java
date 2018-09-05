package com.lyl.study.cloud.security.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

@Data
@ToString
@Accessors(chain = true)
public class JwtSigner {
    // jwt secret
    private String secret = "ThisIsASecret";
    // Token expire in 2 hours.
    private long expire = 7200000L;
    // json serializer
    private ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    private Map<String, Object> serializeObject(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object deserializeObject(String clsName, Map<String, Object> map) {
        try {
            String json = objectMapper.writeValueAsString(map);
            Class<?> aClass = Class.forName(clsName);
            return objectMapper.readValue(json, aClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String serializeToken(Object object) {
        Map<String, Object> claims = serializeObject(object);
        return Jwts.builder()
                .setHeaderParam("cls", claims.getClass())
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Object deserializeToken(String token) {
        try {
            // parse the token.
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            String cls = (String) claimsJws.getHeader().get("cls");
            Map<String, Object> body = claimsJws.getBody();

            // deserialize object to cls instance
            if (cls != null) {
                return deserializeObject(cls, body);
            } else {
                return body;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Invalid Token. " + e.getMessage());
        }
    }
}
