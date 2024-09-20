package br.com.devbean.parametrization.memcache.services;

import br.com.devbean.parametrization.memcache.entities.ParametrizationEntity;
import br.com.devbean.parametrization.memcache.respositories.ParametrizationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
/**
 * Implementação do serviço de parametrização, responsável por gerenciar
 * operações CRUD em parâmetros e aplicar o cache para otimizar a performance.
 */
@Service
public class ParametrizationServiceImpl implements ParametrizationService {

    private final ParametrizationRepository parametrizationRepository;

    /**
     * Construtor que injeta o repositório de parametrização.
     *
     * @param parametrizationRepository Repositório responsável por interagir com a camada de dados.
     */
    public ParametrizationServiceImpl(ParametrizationRepository parametrizationRepository) {
        this.parametrizationRepository = parametrizationRepository;
    }

    /**
     * Busca uma parametrização pelo seu ID. O resultado é armazenado em cache para
     * evitar múltiplas consultas ao banco de dados.
     *
     * @param id O ID da parametrização a ser buscada.
     * @return Um Optional contendo a entidade de parametrização, caso exista.
     */
    @Cacheable(value = "parametrization", key = "#id")
    @Override
    public Optional<ParametrizationEntity> getParametrizatonById(Long id) {
        return parametrizationRepository.findById(id);
    }

    /**
     * Retorna uma lista de todas as parametrizações. Os resultados são armazenados em cache
     * para evitar recarregar os dados repetidamente.
     *
     * @return Uma lista de todas as entidades de parametrização.
     */
    @Cacheable(value = "allParametrizations")
    @Override
    public List<ParametrizationEntity> listAll() {
        return parametrizationRepository.findAll();
    }

    /**
     * Salva uma nova parametrização ou atualiza uma existente. Após a operação, o cache que contém
     * a lista de todas as parametrizações é invalidado para garantir que os dados fiquem atualizados.
     *
     * @param parametrizationEntity A entidade de parametrização a ser salva ou atualizada.
     * @return A entidade de parametrização salva ou atualizada.
     */
    @CacheEvict(value = "allParametrizations", allEntries = true)
    @Transactional
    @Override
    public ParametrizationEntity save(ParametrizationEntity parametrizationEntity) {
        return parametrizationRepository.save(parametrizationEntity);
    }

    /**
     * Ativa ou desativa uma parametrização específica. O cache de parametrização individual
     * e o cache de todas as parametrizações são limpos para garantir consistência.
     *
     * @param enable Booleano que define se a parametrização deve ser ativada (true) ou desativada (false).
     * @param id O ID da parametrização a ser modificada.
     */
    @CacheEvict(value = { "parametrization" , "allParametrizations" }, allEntries = true)
    @Transactional
    @Override
    public void updateParametrizationEnabledState(Boolean enable, Long id) {
        parametrizationRepository.updateParametrizationEnabledState(enable, id);
    }

    /**
     * Remove uma parametrização pelo ID e limpa o cache relacionado.
     * O cache das entradas com o nome "parametrization" e "allParametrizations" será invalidado.
     *
     * @param id O ID da parametrização a ser removida.
     */
    @CacheEvict(value = {"parametrization", "allParametrizations"}, allEntries = true)
    @Transactional
    @Override
    public void delete(Long id) {
        parametrizationRepository.deleteById(id);
    }

    /**
     * Remove uma parametrização pelo ID e NAO limpa o cache relacionado.
     * Esse metodo nao invalida nenhum cache
     *
     * @param id O ID da parametrização a ser removida.
     */
    @Override
    public void deleteNoCache(Long id) {
        parametrizationRepository.deleteById(id);
    }

    /**
     * Recupera uma parametrização do banco de dados com base em sua chave única (key).
     * O resultado é armazenado no cache sob o valor "parametrization" com a chave como identificador de cache.
     *
     * @param key A chave única da parametrização que se deseja buscar.
     * @return Um Optional contendo a parametrização, se encontrada.
     */
    @Cacheable(value = "parametrization", key = "#key")
    @Override
    public Optional<ParametrizationEntity> getParametrizatonByKey(String key) {
        return parametrizationRepository.findByKey(key);
    }

}

