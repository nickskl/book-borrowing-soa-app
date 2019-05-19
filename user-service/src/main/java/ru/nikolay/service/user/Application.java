package ru.nikolay.service.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.nikolay.service.user.domain.Role;
import ru.nikolay.service.user.domain.User;
import ru.nikolay.service.user.repository.UserRepository;

@SpringBootApplication
@EnableSpringDataWebSupport
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        UserRepository repository = context.getBean(UserRepository.class);
        BCryptPasswordEncoder passwordEncoder = context.getBean(BCryptPasswordEncoder.class);

//        User user = new User();
//        user.setLogin("admin");
//        user.setPassword(passwordEncoder.encode("admin"));
//        user.setRole(Role.ADMIN);
//
//        repository.save(user);
//
//        user = new User();
//        user.setLogin("user");
//        user.setPassword(passwordEncoder.encode("test"));
//        user.setRole(Role.USER);
//
//        repository.save(user);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}