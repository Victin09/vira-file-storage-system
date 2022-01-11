package es.vira.infraestructure.security;

import es.vira.infraestructure.security.jwt.JwtProperties;
import es.vira.infraestructure.security.jwt.JwtTokenVerificationFilter;
import es.vira.infraestructure.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;


@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder,
                                 @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                                 JwtProperties jwtProperties,
                                 SecretKey secretKey) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
        this.secretKey = secretKey;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(),
                        jwtProperties,
                        secretKey,
                        "/api/login"))
                .addFilterAfter(new JwtTokenVerificationFilter(jwtProperties, secretKey), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui/**", "/swagger-ui/index.html**", "/webjars/**", "/actuator/health", "/api/auth/register").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/configuration/**", "/v3/api-docs/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
