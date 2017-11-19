package config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.ext.spring.ApplicationContextHolder;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration public class LoggingConfiguration {

    @Bean public static ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean public static LoggerContext loggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    @Bean(initMethod = "start", destroyMethod = "stop") public static PatternLayoutEncoder encoder(LoggerContext ctx) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(ctx);
        encoder.setPattern("%date %-5level [%thread] %logger{36} %m%n");
        return encoder;
    }

    @Bean(initMethod = "start", destroyMethod = "stop") public static ConsoleAppender consoleAppender(LoggerContext ctx,
            PatternLayoutEncoder encoder) {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setContext(ctx);
        appender.setEncoder(encoder);
        return appender;
    }
}
