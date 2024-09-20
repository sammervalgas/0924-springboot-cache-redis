package br.com.devbean.parametrization.memcache.controllers;

import br.com.devbean.parametrization.memcache.entities.ParametrizationEntity;
import br.com.devbean.parametrization.memcache.services.ParametrizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parametrizations")
public class ParametrizationController {

    private final ParametrizationService parametrizationService;

    public ParametrizationController(ParametrizationService parametrizationService) {
        this.parametrizationService = parametrizationService;
    }

    /**
     * Lista todas as parametrizações (Feature Flags).
     *
     * @return Lista de todas as parametrizações.
     */
    @GetMapping
    public ResponseEntity<List<ParametrizationEntity>> listAll() {
        return ResponseEntity.ok(parametrizationService.listAll());
    }

    /**
     * Busca uma parametrização pelo ID.
     *
     * @param id O ID da parametrização.
     * @return A parametrização correspondente.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParametrizationEntity> getParametrizationById(@PathVariable Long id) {
        Optional<ParametrizationEntity> parametrization = parametrizationService.getParametrizatonById(id);
        return parametrization.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Busca uma parametrização pela chave (key).
     *
     * @param key A chave da parametrização.
     * @return A parametrização correspondente.
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<ParametrizationEntity> getParametrizationByKey(@PathVariable String key) {
        Optional<ParametrizationEntity> parametrization = parametrizationService.getParametrizatonByKey(key);
        return parametrization.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cria uma nova parametrização.
     *
     * @param parametrizationEntity O objeto de parametrização a ser criado.
     * @return A parametrização criada.
     */
    @PostMapping
    public ResponseEntity<ParametrizationEntity> createParametrization(@RequestBody ParametrizationEntity parametrizationEntity) {
        ParametrizationEntity savedParametrization = parametrizationService.save(parametrizationEntity);
        return ResponseEntity.ok(savedParametrization);
    }

    /**
     * Atualiza uma parametrização existente.
     *
     * @param id                    O ID da parametrização a ser atualizada.
     * @param parametrizationEntity O objeto atualizado.
     * @return A parametrização atualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParametrizationEntity> updateParametrization(@PathVariable Long id, @RequestBody ParametrizationEntity parametrizationEntity) {
        Optional<ParametrizationEntity> existing = parametrizationService.getParametrizatonById(id);
        if (existing.isPresent()) {
            parametrizationEntity.setId(id); // Assegura que o ID permaneça o mesmo
            ParametrizationEntity updated = parametrizationService.save(parametrizationEntity);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Ativa ou desativa uma parametrização específica.
     *
     * @param id     O ID da parametrização.
     * @param enable O valor booleano que indica se a parametrização deve ser ativada ou desativada.
     */
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> updateParametrizationEnabledState(@PathVariable Long id, @RequestParam Boolean enable) {
        parametrizationService.updateParametrizationEnabledState(enable, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove uma parametrização específica.
     *
     * @param id O ID da parametrização a ser removida.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParametrization(@PathVariable Long id) {
        parametrizationService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * <h1>SEM CACHE</h1>
     * Remove uma parametrização específica.
     *
     *
     * @param id O ID da parametrização a ser removida.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/{id}/nocache")
    public ResponseEntity<Void> deleteParametrizationNoCache(@PathVariable Long id) {
        parametrizationService.deleteNoCache(id);

        return ResponseEntity.noContent().build();
    }
}
