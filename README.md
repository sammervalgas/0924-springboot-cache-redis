# Springboot Parametrization Service with Redis Cache

Este projeto é um exemplo de serviço de parametrização usando **Spring Boot** com **Redis** como cache distribuído. Ele demonstra como armazenar e recuperar parametrizações (como Feature Flags) utilizando cache para otimizar o desempenho.

## Objetivo

O objetivo do projeto é configurar uma arquitetura onde os dados frequentemente acessados sejam mantidos em cache, utilizando Redis para melhorar a performance do sistema e minimizar a carga no banco de dados.

## Conceitos

- **Spring Cache**: Abstração do cache que permite a adição de cache em métodos específicos de serviços.
- **Redis**: Armazena dados em cache de forma distribuída e performática.
- **H2 Database**: Um banco de dados em memória para persistir as parametrizações.
- **Cache Eviction**: Remove itens desatualizados do cache para garantir a consistência com a base de dados.

## Dependências

O projeto utiliza as seguintes dependências:

 ```xml 
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    <scope>runtime</scope>
    </dependency>
  ```
## Configurações do Redis no `application.yml`

O Redis é configurado diretamente no arquivo `application.yml` para ser utilizado como cache:
```yml
spring:
  cache:
    type: redis
    redis:
      host: localhost
      port: 6379

    datasource:
      url: jdbc:h2:mem:parametrizationdb;DB_CLOSE_ON_EXIT=FALSE
      driver-class-name: org.h2.Driver
      username: sa
      password:
```
## Estrutura do Projeto

O projeto está organizado em camadas:

- **Entity**: Define o mapeamento das parametrizações com o banco de dados.
- **Service**: Contém a lógica de negócios, onde o cache é aplicado.
- **Controller**: Expõe endpoints REST para interagir com as parametrizações.
- **Cache Configurations**: Configura a estratégia de cache (Redis ou local).

## Iniciando o Projeto

### 1. Clonar o repositório

Faça o clone deste repositório localmente:
```bash
git clone https://github.com...
cd parametrization-service
```
### 2. Docker para Redis

Certifique-se de que o Redis esteja rodando. Você pode usar Docker para isso:
```bash
cd docker
docker-compose up -d
```
### 3. Rodar o projeto

Inicie o projeto com o Spring Boot:
```bash
./mvnw spring-boot:run
```
### 4. Acessando o H2 Console

Para acessar o console do H2 e visualizar as tabelas de parametrização, acesse a seguinte URL:
```bash
http://localhost:8080/h2-console
```
Use as configurações abaixo para acessar o banco:

- **JDBC URL**: `jdbc:h2:mem:parametrizationdb`
- **Username**: `sa`
- **Password**: (deixe em branco)

## Exemplo de Endpoints

### Listar todas as parametrizações

Este endpoint lista todas as parametrizações, utilizando o cache se já houver dados:

GET /api/parametrizations

### Adicionar uma nova parametrização

Este endpoint adiciona uma nova parametrização e invalida o cache existente para atualizar com os novos dados.

POST /api/parametrizations

### Excluir uma parametrização

Este endpoint exclui uma parametrização específica e remove o cache relacionado a ela.

DELETE /api/parametrizations/{id}

## Configurações de Cache

A classe `RedisConfiguration` configura o cache Redis e local:
```java

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

```
## Configuração do Redis Template

O Redis Template permite a serialização dos dados em JSON:

```java
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
```

## DataLoader para Parametrizações

Este `DataLoader` inicializa o banco de dados com valores padrão para parametrizações (Feature Flags) no momento em que a aplicação é iniciada.

```java
@Component
public class ParametrizationDataLoader {

    /**
     * Inicializa o banco de dados com valores padrão para parametrizações (Feature Flags).
     * Executado quando a aplicação é iniciada.
     *
     * @param parametrizationRepository O repositório para interagir com a base de dados de parametrizações.
     * @return Um CommandLineRunner que carrega os dados.
     */
    @Bean
    CommandLineRunner loadParametrizationData(ParametrizationRepository parametrizationRepository) {
        return args -> {
            if (parametrizationRepository.count() == 0) {
                // Insere os dados de parametrizações (Feature Flags) iniciais
                parametrizationRepository.saveAll(List.of(
                        new ParametrizationEntity(null, "NEW_PAYMENT_GATEWAY", "Enable New Payment Gateway", true, Date.from(Instant.now())),

                        new ParametrizationEntity(null, "BETA_FEATURES", "Enable Beta Features", false, Date.from(Instant.now())),

                        new ParametrizationEntity(null, "EMAIL_NOTIFICATIONS", "Enable Email Notifications", true, Date.from(Instant.now())),

                        new ParametrizationEntity(null, "DARK_MODE", "Enable Dark Mode", false, Date.from(Instant.now()))
                ));
                System.out.println("Parametrization data loaded.");
            } else {
                System.out.println("Parametrization data already exists.");
            }
        };
    }
}
```
Este código carrega automaticamente as parametrizações (Feature Flags) ao iniciar o aplicativo, garantindo que haja dados iniciais no banco de dados H2. Se já houver dados no banco, a inserção não será repetida.

## Contribuições

Sinta-se à vontade para fazer um fork do projeto e enviar Pull Requests.

## Autores

- Sammer Valgas | XGH Expert