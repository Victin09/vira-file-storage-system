package es.vira.infraestructure.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.vira.infraestructure.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Log4j2
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                      JwtProperties jwtProperties,
                                                      SecretKey secretKey,
                                                      String filterProcessesUrl) {
        this.authenticationManager = authenticationManager;
        this.jwtProperties = jwtProperties;
        this.secretKey = secretKey;
        setFilterProcessesUrl(filterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            UsernameAndPasswordAuthenticationRequest authenticationRequest = getAuthenticationRequest(request);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    private UsernameAndPasswordAuthenticationRequest getAuthenticationRequest(HttpServletRequest request) throws IOException {
        return new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        String authorities = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim(SecurityConstants.AUTHORITIES, authorities)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(jwtProperties.getTokenExpirationHours(), ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
        response.addHeader(jwtProperties.getAuthorizationHeaderName(), jwtProperties.getTokenPrefix() + token);
    }
}
