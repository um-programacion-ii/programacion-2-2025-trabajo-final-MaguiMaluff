package ar.edu.um.service;

import ar.edu.um.domain.EventoIntegrante;
import ar.edu.um.repository.EventoIntegranteRepository;
import ar.edu.um.service.dto.EventoIntegranteDTO;
import ar.edu.um.service.mapper.EventoIntegranteMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.domain.EventoIntegrante}.
 */
@Service
@Transactional
public class EventoIntegranteService {

    private static final Logger LOG = LoggerFactory.getLogger(EventoIntegranteService.class);

    private final EventoIntegranteRepository eventoIntegranteRepository;

    private final EventoIntegranteMapper eventoIntegranteMapper;

    public EventoIntegranteService(EventoIntegranteRepository eventoIntegranteRepository, EventoIntegranteMapper eventoIntegranteMapper) {
        this.eventoIntegranteRepository = eventoIntegranteRepository;
        this.eventoIntegranteMapper = eventoIntegranteMapper;
    }

    /**
     * Save a eventoIntegrante.
     *
     * @param eventoIntegranteDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoIntegranteDTO save(EventoIntegranteDTO eventoIntegranteDTO) {
        LOG.debug("Request to save EventoIntegrante : {}", eventoIntegranteDTO);
        EventoIntegrante eventoIntegrante = eventoIntegranteMapper.toEntity(eventoIntegranteDTO);
        eventoIntegrante = eventoIntegranteRepository.save(eventoIntegrante);
        return eventoIntegranteMapper.toDto(eventoIntegrante);
    }

    /**
     * Update a eventoIntegrante.
     *
     * @param eventoIntegranteDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoIntegranteDTO update(EventoIntegranteDTO eventoIntegranteDTO) {
        LOG.debug("Request to update EventoIntegrante : {}", eventoIntegranteDTO);
        EventoIntegrante eventoIntegrante = eventoIntegranteMapper.toEntity(eventoIntegranteDTO);
        eventoIntegrante = eventoIntegranteRepository.save(eventoIntegrante);
        return eventoIntegranteMapper.toDto(eventoIntegrante);
    }

    /**
     * Partially update a eventoIntegrante.
     *
     * @param eventoIntegranteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventoIntegranteDTO> partialUpdate(EventoIntegranteDTO eventoIntegranteDTO) {
        LOG.debug("Request to partially update EventoIntegrante : {}", eventoIntegranteDTO);

        return eventoIntegranteRepository
            .findById(eventoIntegranteDTO.getId())
            .map(existingEventoIntegrante -> {
                eventoIntegranteMapper.partialUpdate(existingEventoIntegrante, eventoIntegranteDTO);

                return existingEventoIntegrante;
            })
            .map(eventoIntegranteRepository::save)
            .map(eventoIntegranteMapper::toDto);
    }

    /**
     * Get all the eventoIntegrantes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EventoIntegranteDTO> findAll() {
        LOG.debug("Request to get all EventoIntegrantes");
        return eventoIntegranteRepository
            .findAll()
            .stream()
            .map(eventoIntegranteMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one eventoIntegrante by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventoIntegranteDTO> findOne(Long id) {
        LOG.debug("Request to get EventoIntegrante : {}", id);
        return eventoIntegranteRepository.findById(id).map(eventoIntegranteMapper::toDto);
    }

    /**
     * Delete the eventoIntegrante by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EventoIntegrante : {}", id);
        eventoIntegranteRepository.deleteById(id);
    }
}
