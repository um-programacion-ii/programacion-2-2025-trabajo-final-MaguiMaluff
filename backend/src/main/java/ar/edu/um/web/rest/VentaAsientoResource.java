package ar.edu.um.web.rest;

import ar.edu.um.repository.VentaAsientoRepository;
import ar.edu.um.service.VentaAsientoService;
import ar.edu.um.service.dto.VentaAsientoDTO;
import ar.edu.um.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ar.edu.um.domain.VentaAsiento}.
 */
@RestController
@RequestMapping("/api/venta-asientos")
public class VentaAsientoResource {

    private static final Logger LOG = LoggerFactory.getLogger(VentaAsientoResource.class);

    private static final String ENTITY_NAME = "ventaAsiento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VentaAsientoService ventaAsientoService;

    private final VentaAsientoRepository ventaAsientoRepository;

    public VentaAsientoResource(VentaAsientoService ventaAsientoService, VentaAsientoRepository ventaAsientoRepository) {
        this.ventaAsientoService = ventaAsientoService;
        this.ventaAsientoRepository = ventaAsientoRepository;
    }

    /**
     * {@code POST  /venta-asientos} : Create a new ventaAsiento.
     *
     * @param ventaAsientoDTO the ventaAsientoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ventaAsientoDTO, or with status {@code 400 (Bad Request)} if the ventaAsiento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VentaAsientoDTO> createVentaAsiento(@Valid @RequestBody VentaAsientoDTO ventaAsientoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save VentaAsiento : {}", ventaAsientoDTO);
        if (ventaAsientoDTO.getId() != null) {
            throw new BadRequestAlertException("A new ventaAsiento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ventaAsientoDTO = ventaAsientoService.save(ventaAsientoDTO);
        return ResponseEntity.created(new URI("/api/venta-asientos/" + ventaAsientoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, ventaAsientoDTO.getId().toString()))
            .body(ventaAsientoDTO);
    }

    /**
     * {@code PUT  /venta-asientos/:id} : Updates an existing ventaAsiento.
     *
     * @param id the id of the ventaAsientoDTO to save.
     * @param ventaAsientoDTO the ventaAsientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ventaAsientoDTO,
     * or with status {@code 400 (Bad Request)} if the ventaAsientoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ventaAsientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VentaAsientoDTO> updateVentaAsiento(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VentaAsientoDTO ventaAsientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update VentaAsiento : {}, {}", id, ventaAsientoDTO);
        if (ventaAsientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ventaAsientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ventaAsientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ventaAsientoDTO = ventaAsientoService.update(ventaAsientoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ventaAsientoDTO.getId().toString()))
            .body(ventaAsientoDTO);
    }

    /**
     * {@code PATCH  /venta-asientos/:id} : Partial updates given fields of an existing ventaAsiento, field will ignore if it is null
     *
     * @param id the id of the ventaAsientoDTO to save.
     * @param ventaAsientoDTO the ventaAsientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ventaAsientoDTO,
     * or with status {@code 400 (Bad Request)} if the ventaAsientoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ventaAsientoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ventaAsientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VentaAsientoDTO> partialUpdateVentaAsiento(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VentaAsientoDTO ventaAsientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update VentaAsiento partially : {}, {}", id, ventaAsientoDTO);
        if (ventaAsientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ventaAsientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ventaAsientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VentaAsientoDTO> result = ventaAsientoService.partialUpdate(ventaAsientoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ventaAsientoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /venta-asientos} : get all the ventaAsientos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ventaAsientos in body.
     */
    @GetMapping("")
    public List<VentaAsientoDTO> getAllVentaAsientos() {
        LOG.debug("REST request to get all VentaAsientos");
        return ventaAsientoService.findAll();
    }

    /**
     * {@code GET  /venta-asientos/:id} : get the "id" ventaAsiento.
     *
     * @param id the id of the ventaAsientoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ventaAsientoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaAsientoDTO> getVentaAsiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to get VentaAsiento : {}", id);
        Optional<VentaAsientoDTO> ventaAsientoDTO = ventaAsientoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ventaAsientoDTO);
    }

    /**
     * {@code DELETE  /venta-asientos/:id} : delete the "id" ventaAsiento.
     *
     * @param id the id of the ventaAsientoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVentaAsiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete VentaAsiento : {}", id);
        ventaAsientoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
