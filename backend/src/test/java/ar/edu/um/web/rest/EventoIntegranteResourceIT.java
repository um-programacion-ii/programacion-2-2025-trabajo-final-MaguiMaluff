package ar.edu.um.web.rest;

import static ar.edu.um.domain.EventoIntegranteAsserts.*;
import static ar.edu.um.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.IntegrationTest;
import ar.edu.um.domain.Evento;
import ar.edu.um.domain.EventoIntegrante;
import ar.edu.um.repository.EventoIntegranteRepository;
import ar.edu.um.service.dto.EventoIntegranteDTO;
import ar.edu.um.service.mapper.EventoIntegranteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EventoIntegranteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventoIntegranteResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_IDENTIFICACION = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFICACION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/evento-integrantes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventoIntegranteRepository eventoIntegranteRepository;

    @Autowired
    private EventoIntegranteMapper eventoIntegranteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventoIntegranteMockMvc;

    private EventoIntegrante eventoIntegrante;

    private EventoIntegrante insertedEventoIntegrante;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventoIntegrante createEntity(EntityManager em) {
        EventoIntegrante eventoIntegrante = new EventoIntegrante()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .identificacion(DEFAULT_IDENTIFICACION);
        // Add required entity
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            evento = EventoResourceIT.createEntity();
            em.persist(evento);
            em.flush();
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        eventoIntegrante.setEvento(evento);
        return eventoIntegrante;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventoIntegrante createUpdatedEntity(EntityManager em) {
        EventoIntegrante updatedEventoIntegrante = new EventoIntegrante()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .identificacion(UPDATED_IDENTIFICACION);
        // Add required entity
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            evento = EventoResourceIT.createUpdatedEntity();
            em.persist(evento);
            em.flush();
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        updatedEventoIntegrante.setEvento(evento);
        return updatedEventoIntegrante;
    }

    @BeforeEach
    void initTest() {
        eventoIntegrante = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedEventoIntegrante != null) {
            eventoIntegranteRepository.delete(insertedEventoIntegrante);
            insertedEventoIntegrante = null;
        }
    }

    @Test
    @Transactional
    void createEventoIntegrante() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EventoIntegrante
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);
        var returnedEventoIntegranteDTO = om.readValue(
            restEventoIntegranteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoIntegranteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventoIntegranteDTO.class
        );

        // Validate the EventoIntegrante in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEventoIntegrante = eventoIntegranteMapper.toEntity(returnedEventoIntegranteDTO);
        assertEventoIntegranteUpdatableFieldsEquals(returnedEventoIntegrante, getPersistedEventoIntegrante(returnedEventoIntegrante));

        insertedEventoIntegrante = returnedEventoIntegrante;
    }

    @Test
    @Transactional
    void createEventoIntegranteWithExistingId() throws Exception {
        // Create the EventoIntegrante with an existing ID
        eventoIntegrante.setId(1L);
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventoIntegranteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoIntegranteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventoIntegrante.setNombre(null);

        // Create the EventoIntegrante, which fails.
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        restEventoIntegranteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoIntegranteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApellidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventoIntegrante.setApellido(null);

        // Create the EventoIntegrante, which fails.
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        restEventoIntegranteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoIntegranteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventoIntegrantes() throws Exception {
        // Initialize the database
        insertedEventoIntegrante = eventoIntegranteRepository.saveAndFlush(eventoIntegrante);

        // Get all the eventoIntegranteList
        restEventoIntegranteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventoIntegrante.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].identificacion").value(hasItem(DEFAULT_IDENTIFICACION)));
    }

    @Test
    @Transactional
    void getEventoIntegrante() throws Exception {
        // Initialize the database
        insertedEventoIntegrante = eventoIntegranteRepository.saveAndFlush(eventoIntegrante);

        // Get the eventoIntegrante
        restEventoIntegranteMockMvc
            .perform(get(ENTITY_API_URL_ID, eventoIntegrante.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventoIntegrante.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO))
            .andExpect(jsonPath("$.identificacion").value(DEFAULT_IDENTIFICACION));
    }

    @Test
    @Transactional
    void getNonExistingEventoIntegrante() throws Exception {
        // Get the eventoIntegrante
        restEventoIntegranteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventoIntegrante() throws Exception {
        // Initialize the database
        insertedEventoIntegrante = eventoIntegranteRepository.saveAndFlush(eventoIntegrante);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventoIntegrante
        EventoIntegrante updatedEventoIntegrante = eventoIntegranteRepository.findById(eventoIntegrante.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventoIntegrante are not directly saved in db
        em.detach(updatedEventoIntegrante);
        updatedEventoIntegrante.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).identificacion(UPDATED_IDENTIFICACION);
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(updatedEventoIntegrante);

        restEventoIntegranteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventoIntegranteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventoIntegranteDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventoIntegranteToMatchAllProperties(updatedEventoIntegrante);
    }

    @Test
    @Transactional
    void putNonExistingEventoIntegrante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoIntegrante.setId(longCount.incrementAndGet());

        // Create the EventoIntegrante
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoIntegranteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventoIntegranteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventoIntegranteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventoIntegrante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoIntegrante.setId(longCount.incrementAndGet());

        // Create the EventoIntegrante
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoIntegranteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventoIntegranteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventoIntegrante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoIntegrante.setId(longCount.incrementAndGet());

        // Create the EventoIntegrante
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoIntegranteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoIntegranteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventoIntegranteWithPatch() throws Exception {
        // Initialize the database
        insertedEventoIntegrante = eventoIntegranteRepository.saveAndFlush(eventoIntegrante);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventoIntegrante using partial update
        EventoIntegrante partialUpdatedEventoIntegrante = new EventoIntegrante();
        partialUpdatedEventoIntegrante.setId(eventoIntegrante.getId());

        partialUpdatedEventoIntegrante.nombre(UPDATED_NOMBRE);

        restEventoIntegranteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventoIntegrante.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventoIntegrante))
            )
            .andExpect(status().isOk());

        // Validate the EventoIntegrante in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoIntegranteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEventoIntegrante, eventoIntegrante),
            getPersistedEventoIntegrante(eventoIntegrante)
        );
    }

    @Test
    @Transactional
    void fullUpdateEventoIntegranteWithPatch() throws Exception {
        // Initialize the database
        insertedEventoIntegrante = eventoIntegranteRepository.saveAndFlush(eventoIntegrante);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventoIntegrante using partial update
        EventoIntegrante partialUpdatedEventoIntegrante = new EventoIntegrante();
        partialUpdatedEventoIntegrante.setId(eventoIntegrante.getId());

        partialUpdatedEventoIntegrante.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).identificacion(UPDATED_IDENTIFICACION);

        restEventoIntegranteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventoIntegrante.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventoIntegrante))
            )
            .andExpect(status().isOk());

        // Validate the EventoIntegrante in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoIntegranteUpdatableFieldsEquals(
            partialUpdatedEventoIntegrante,
            getPersistedEventoIntegrante(partialUpdatedEventoIntegrante)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEventoIntegrante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoIntegrante.setId(longCount.incrementAndGet());

        // Create the EventoIntegrante
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoIntegranteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventoIntegranteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventoIntegranteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventoIntegrante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoIntegrante.setId(longCount.incrementAndGet());

        // Create the EventoIntegrante
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoIntegranteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventoIntegranteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventoIntegrante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventoIntegrante.setId(longCount.incrementAndGet());

        // Create the EventoIntegrante
        EventoIntegranteDTO eventoIntegranteDTO = eventoIntegranteMapper.toDto(eventoIntegrante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoIntegranteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventoIntegranteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventoIntegrante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventoIntegrante() throws Exception {
        // Initialize the database
        insertedEventoIntegrante = eventoIntegranteRepository.saveAndFlush(eventoIntegrante);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the eventoIntegrante
        restEventoIntegranteMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventoIntegrante.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventoIntegranteRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected EventoIntegrante getPersistedEventoIntegrante(EventoIntegrante eventoIntegrante) {
        return eventoIntegranteRepository.findById(eventoIntegrante.getId()).orElseThrow();
    }

    protected void assertPersistedEventoIntegranteToMatchAllProperties(EventoIntegrante expectedEventoIntegrante) {
        assertEventoIntegranteAllPropertiesEquals(expectedEventoIntegrante, getPersistedEventoIntegrante(expectedEventoIntegrante));
    }

    protected void assertPersistedEventoIntegranteToMatchUpdatableProperties(EventoIntegrante expectedEventoIntegrante) {
        assertEventoIntegranteAllUpdatablePropertiesEquals(
            expectedEventoIntegrante,
            getPersistedEventoIntegrante(expectedEventoIntegrante)
        );
    }
}
