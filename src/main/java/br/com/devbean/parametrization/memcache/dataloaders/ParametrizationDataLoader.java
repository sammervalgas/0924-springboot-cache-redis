package br.com.devbean.parametrization.memcache.dataloaders;

import br.com.devbean.parametrization.memcache.entities.ParametrizationEntity;
import br.com.devbean.parametrization.memcache.respositories.ParametrizationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.time.Instant;
import java.util.List;

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
