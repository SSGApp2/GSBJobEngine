package com.app2.engine.spring;

import com.app2.engine.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfiguration {

    @Autowired
    NotificationSettingRepository notificationSettingRepository;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

    @Bean
    public String getNotiTimeProcessTwo(){
        return notificationSettingRepository.findByProcessType("2").getNotiTime();
    }

    @Bean
    public String getNotiTimeProcessThree(){
        return notificationSettingRepository.findByProcessType("3").getNotiTime();
    }

    @Bean
    public String getNotiTimeProcessFour(){
        return notificationSettingRepository.findByProcessType("4").getNotiTime();
    }

    @Bean
    public String getNotiTimeProcessSix(){
        return notificationSettingRepository.findByProcessType("6").getNotiTime();
    }

    @Bean
    public String getNotiTimeProcessSeven(){
        return notificationSettingRepository.findByProcessType("7").getNotiTime();
    }

    @Bean
    public String getNotiTimeProcessEight(){
        return notificationSettingRepository.findByProcessType("8").getNotiTime();
    }

}
