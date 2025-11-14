package ar.edu.um.web.rest;

import ar.edu.um.repository.EventoIntegranteRepository;
import ar.edu.um.service.EventoIntegranteService;
import ar.edu.um.service.dto.EventoIntegranteDTO;
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
 * REST controller for managing {@link ar.edu.um.domain.EventoIntegrante}.
 */
@RestController
@RequestMapping("/api/evento-integrantes")
public class EventoIntegranteResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventoIntegranteResource.class);

    private static final String ENTITY_NAME = "eventoIntegrante";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventoIntegranteService eventoIntegranteService;

    private final EventoIntegranteRepository eventoIntegranteRepository;

    public EventoIntegranteResource(
        EventoIntegranteService eventoIntegranteService,
        EventoIntegranteRepository eventoIntegranteRepository
    ) {
        this.eventoIntegranteService = eventoIntegranteService;
        this.eventoIntegranteRepository = eventoIntegranteRepository;
    }

    /**
     * {@code POST  /evento-integrantes} : Create a new eventoIntegrante.
     *
     * @param eventoIntegranteDTO the eventoIntegranteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventoIntegranteDTO, or with status {@code 400 (Bad Request)} if the eventoIntegrante has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EventoIntegranteDTO> createEventoIntegrante(@Valid @RequestBody EventoIntegranteDTO eventoIntegranteDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EventoIntegrante : {}", eventoIntegranteDTO);
        if (eventoIntegranteDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventoIntegrante cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventoIntegranteDTO = eventoIntegranteService.save(eventoIntegranteDTO);
        return ResponseEntity.created(new URI("/api/evento-integrantes/" + eventoIntegranteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, eventoIntegranteDTO.getId().toString()))
            .body(eventoIntegranteDTO);
    }

    /**
     * {@code PUT  /evento-integrantes/:id} : Updates an existing eventoIntegrante.
     *
     * @param id the id of the eventoIntegranteDTO to save.
     * @param eventoIntegranteDTO the eventoIntegranteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventoIntegranteDTO,
     * or with status {@code 400 (Bad Request)} if the eventoIntegranteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventoIntegranteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventoIntegranteDTO> updateEventoIntegrante(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventoIntegranteDTO eventoIntegranteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EventoIntegrante : {}, {}", id, eventoIntegranteDTO);
        if (eventoIntegranteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventoIntegranteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventoIntegranteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventoIntegranteDTO = eventoIntegranteService.update(eventoIntegranteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventoIntegranteDTO.getId().toString()))
            .body(eventoIntegranteDTO);
    }

    /**
     * {@code PATCH  /evento-integrantes/:id} : Partial updates given fields of an existing eventoIntegrante, field will ignore if it is null
     *
     * @param id the id of the eventoIntegranteDTO to save.
     * @param eventoIntegranteDTO the eventoIntegranteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventoIntegranteDTO,
     * or with status {@code 400 (Bad Request)} if the eventoIntegranteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventoIntegranteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventoIntegranteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventoIntegranteDTO> partialUpdateEventoIntegrante(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventoIntegranteDTO eventoIntegranteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EventoIntegrante partially : {}, {}", id, eventoIntegranteDTO);
        if (eventoIntegranteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventoIntegranteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventoIntegranteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventoIntegranteDTO> result = eventoIntegranteService.partialUpdate(eventoIntegranteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventoIntegranteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /evento-integrantes} : get all the eventoIntegrantes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventoIntegrantes in body.
     */
    @GetMapping("")
    public List<EventoIntegranteDTO> getAllEventoIntegrantes() {
        LOG.debug("REST request to get all EventoIntegrantes");
        return eventoIntegranteService.findAll();
    }

    /**
     * {@code GET  /evento-integrantes/:id} : get the "id" eventoIntegrante.
     *
     * @param id the id of the eventoIntegranteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventoIntegranteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventoIntegranteDTO> getEventoIntegrante(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EventoIntegrante : {}", id);
        Optional<EventoIntegranteDTO> eventoIntegranteDTO = eventoIntegranteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventoIntegranteDTO);
    }

    /**
     * {@code DELETE  /evento-integrantes/:id} : delete the "id" eventoIntegrante.
     *
     * @param id the id of the eventoIntegranteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventoIntegrante(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EventoIntegrante : {}", id);
        eventoIntegranteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
