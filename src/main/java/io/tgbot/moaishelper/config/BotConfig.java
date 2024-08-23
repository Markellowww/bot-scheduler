package io.tgbot.moaishelper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Objects;

/**
 * @Authors: Markelloww & YDK
 */

@Configuration
@PropertySource("classpath:application.properties")
public class BotConfig {

    @Value("${bot.name}")
    String name;
    @Value("${bot.token}")
    String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "BotConfig{" +
                "name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotConfig botConfig = (BotConfig) o;
        return Objects.equals(name, botConfig.name) && Objects.equals(token, botConfig.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, token);
    }
}
