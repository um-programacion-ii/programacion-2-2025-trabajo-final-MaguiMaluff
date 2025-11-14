package ar.edu.um.service;

import ar.edu.um.domain.VentaAsiento;
import ar.edu.um.repository.VentaAsientoRepository;
import ar.edu.um.service.dto.VentaAsientoDTO;
import ar.edu.um.service.mapper.VentaAsientoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.domain.VentaAsiento}.
 */
@Service
@Transactional
public class VentaAsientoService {

    private static final Logger LOG = LoggerFactory.getLogger(VentaAsientoService.class);

    private final VentaAsientoRepository ventaAsientoRepository;

    private final VentaAsientoMapper ventaAsientoMapper;

    public VentaAsientoService(VentaAsientoRepository ventaAsientoRepository, VentaAsientoMapper ventaAsientoMapper) {
        this.ventaAsientoRepository = ventaAsientoRepository;
        this.ventaAsientoMapper = ventaAsientoMapper;
    }

    /**
     * Save a ventaAsiento.
     *
     * @param ventaAsientoDTO the entity to save.
     * @return the persisted entity.
     */
    public VentaAsientoDTO save(VentaAsientoDTO ventaAsientoDTO) {
        LOG.debug("Request to save VentaAsiento : {}", ventaAsientoDTO);
        VentaAsiento ventaAsiento = ventaAsientoMapper.toEntity(ventaAsientoDTO);
        ventaAsiento = ventaAsientoRepository.save(ventaAsiento);
        return ventaAsientoMapper.toDto(ventaAsiento);
    }

    /**
     * Update a ventaAsiento.
     *
     * @param ventaAsientoDTO the entity to save.
     * @return the persisted entity.
     */
    public VentaAsientoDTO update(VentaAsientoDTO ventaAsientoDTO) {
        LOG.debug("Request to update VentaAsiento : {}", ventaAsientoDTO);
        VentaAsiento ventaAsiento = ventaAsientoMapper.toEntity(ventaAsientoDTO);
        ventaAsiento = ventaAsientoRepository.save(ventaAsiento);
        return ventaAsientoMapper.toDto(ventaAsiento);
    }

    /**
     * Partially update a ventaAsiento.
     *
     * @param ventaAsientoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VentaAsientoDTO> partialUpdate(VentaAsientoDTO ventaAsientoDTO) {
        LOG.debug("Request to partially update VentaAsiento : {}", ventaAsientoDTO);

        return ventaAsientoRepository
            .findById(ventaAsientoDTO.getId())
            .map(existingVentaAsiento -> {
                ventaAsientoMapper.partialUpdate(existingVentaAsiento, ventaAsientoDTO);

                return existingVentaAsiento;
            })
            .map(ventaAsientoRepository::save)
            .map(ventaAsientoMapper::toDto);
    }

    /**
     * Get all the ventaAsientos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<VentaAsientoDTO> findAll() {
        LOG.debug("Request to get all VentaAsientos");
        return ventaAsientoRepository.findAll().stream().map(ventaAsientoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one ventaAsiento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VentaAsientoDTO> findOne(Long id) {
        LOG.debug("Request to get VentaAsiento : {}", id);
        return ventaAsientoRepository.findById(id).map(ventaAsientoMapper::toDto);
    }

    /**
     * Delete the ventaAsiento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete VentaAsiento : {}", id);
        ventaAsientoRepository.deleteById(id);
    }
}
