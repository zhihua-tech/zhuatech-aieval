/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval.config;
import org.springframework.beans.factory.annotation.Value;import org.springframework.context.annotation.*;import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.core.userdetails.*;import org.springframework.security.provisioning.InMemoryUserDetailsManager;import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.security.web.SecurityFilterChain;
@Configuration@EnableMethodSecurity public class SecurityConfig{
 @Bean PasswordEncoder encoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService users(PasswordEncoder e,@Value("${app.security.admin-password}")String a,@Value("${app.security.auditor-password}")String u){return new InMemoryUserDetailsManager(User.withUsername("admin").password(e.encode(a)).roles("ADMIN","EVALUATOR","AUDITOR").build(),User.withUsername("auditor").password(e.encode(u)).roles("AUDITOR").build());}
 @Bean SecurityFilterChain chain(HttpSecurity h)throws Exception{return h.csrf(x->x.disable()).cors(x->{}).authorizeHttpRequests(x->x.requestMatchers("/error").permitAll().anyRequest().authenticated()).httpBasic(x->{}).build();}
}
