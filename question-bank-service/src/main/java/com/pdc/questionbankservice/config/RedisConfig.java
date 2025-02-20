package com.pdc.questionbankservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule timeModule = new JavaTimeModule();

        // Custom serializer for LocalDateTime
        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        objectMapper.registerModule(timeModule);
        return objectMapper;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration(ObjectMapper redisObjectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                //
                .withCacheConfiguration("question", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("questionList", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("chapter", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("chapterList", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("chapterStats", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                // answers
                .withCacheConfiguration("answer", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("answerList", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
                // questions
                .withCacheConfiguration("questionsByChapter", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("questionsByFaculty", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration("questionsByFacultyAndChapter", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration("searchQuestions", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("questionOwnership", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("validateQuestions", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)));
    }
}