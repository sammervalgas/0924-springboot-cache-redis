package br.com.devbean.parametrization.memcache.services;

import br.com.devbean.parametrization.memcache.entities.ParametrizationEntity;

import java.util.List;
import java.util.Optional;

public interface ParametrizationService {

    List<ParametrizationEntity> listAll();

    ParametrizationEntity save(ParametrizationEntity parametrizationEntity);

    Optional<ParametrizationEntity> getParametrizatonById(Long id);

    Optional<ParametrizationEntity> getParametrizatonByKey(String key);

    void updateParametrizationEnabledState(Boolean enable, Long id);

    void delete(Long id);

    void deleteNoCache(Long id);
}
