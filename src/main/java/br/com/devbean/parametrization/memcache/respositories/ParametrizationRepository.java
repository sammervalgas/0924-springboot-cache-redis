package br.com.devbean.parametrization.memcache.respositories;

import br.com.devbean.parametrization.memcache.entities.ParametrizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParametrizationRepository extends JpaRepository<ParametrizationEntity, Long> {

    @Query("UPDATE ParametrizationEntity PE SET PE.enabled = :enable WHERE PE.id =:id")
    void updateParametrizationEnabledState(@Param("enable") Boolean enable, @Param("id") Long id);

    Optional<ParametrizationEntity> findByKey(String key);
}
