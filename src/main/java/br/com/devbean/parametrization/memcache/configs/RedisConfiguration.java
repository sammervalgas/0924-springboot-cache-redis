package br.com.devbean.parametrization.memcache.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    // Configura o RedisTemplate para usar JSON como serializador
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Configurando o ObjectMapper para o Jackson2JsonRedisSerializer
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Ignorar campos nulos
        objectMapper.registerModule(new JavaTimeModule()); // Suporte para Java 8 Time API
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Datas no formato ISO-8601

        // Configurando o serializador com o ObjectMapper
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // Configurando o RedisTemplate
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }

    /**
     * Configura o Cache Manager para utilizar o Redis como mecanismo de cache.
     * O Redis é uma opção de cache distribuído, ideal para ambientes com múltiplas instâncias de uma aplicação.
     *
     * @param redisConnectionFactory - Fábrica de conexões com o Redis, que é injetada automaticamente pelo Spring.
     * @return CacheManager - Gerenciador de cache que será usado para armazenar os dados no Redis.
     */
    @Bean
    @Primary
    @Qualifier("redis")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    /**
     * Configura o Cache Manager local, utilizando o cache em memória (SimpleCache).
     * Esse cache é ideal para uso em ambientes de desenvolvimento ou em casos onde não se necessita de cache distribuído.
     *
     * @return CacheManager - Gerenciador de cache simples que armazena dados em memória local.
     */
    @Bean
    @Qualifier("simple")
    public CacheManager simpleCacheManager() {
        // Cria um ConcurrentMapCacheManager com dois caches nomeados: 'parametrization' e 'allParametrizations'.
        // Esses caches são mantidos em memória e são adequados para ambientes com uma única instância de aplicação.
        return new ConcurrentMapCacheManager("parametrization", "allParametrizations");
    }
}

