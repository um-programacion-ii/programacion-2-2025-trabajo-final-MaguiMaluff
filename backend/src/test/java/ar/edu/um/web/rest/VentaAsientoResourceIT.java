package ar.edu.um.web.rest;

import static ar.edu.um.domain.VentaAsientoAsserts.*;
import static ar.edu.um.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.IntegrationTest;
import ar.edu.um.domain.Venta;
import ar.edu.um.domain.VentaAsiento;
import ar.edu.um.repository.VentaAsientoRepository;
import ar.edu.um.service.dto.VentaAsientoDTO;
import ar.edu.um.service.mapper.VentaAsientoMapper;
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
 * Integration tests for the {@link VentaAsientoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VentaAsientoResourceIT {

    private static final Integer DEFAULT_FILA = 1;
    private static final Integer UPDATED_FILA = 2;

    private static final Integer DEFAULT_COLUMNA = 1;
    private static final Integer UPDATED_COLUMNA = 2;

    private static final String DEFAULT_PERSONA = "AAAAAAAAAA";
    private static final String UPDATED_PERSONA = "BBBBBBBBBB";

    private static final String DEFAULT_ESTADO = "AAAAAAAAAA";
    private static final String UPDATED_ESTADO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/venta-asientos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VentaAsientoRepository ventaAsientoRepository;

    @Autowired
    private VentaAsientoMapper ventaAsientoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVentaAsientoMockMvc;

    private VentaAsiento ventaAsiento;

    private VentaAsiento insertedVentaAsiento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VentaAsiento createEntity(EntityManager em) {
        VentaAsiento ventaAsiento = new VentaAsiento()
            .fila(DEFAULT_FILA)
            .columna(DEFAULT_COLUMNA)
            .persona(DEFAULT_PERSONA)
            .estado(DEFAULT_ESTADO);
        // Add required entity
        Venta venta;
        if (TestUtil.findAll(em, Venta.class).isEmpty()) {
            venta = VentaResourceIT.createEntity(em);
            em.persist(venta);
            em.flush();
        } else {
            venta = TestUtil.findAll(em, Venta.class).get(0);
        }
        ventaAsiento.setVenta(venta);
        return ventaAsiento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VentaAsiento createUpdatedEntity(EntityManager em) {
        VentaAsiento updatedVentaAsiento = new VentaAsiento()
            .fila(UPDATED_FILA)
            .columna(UPDATED_COLUMNA)
            .persona(UPDATED_PERSONA)
            .estado(UPDATED_ESTADO);
        // Add required entity
        Venta venta;
        if (TestUtil.findAll(em, Venta.class).isEmpty()) {
            venta = VentaResourceIT.createUpdatedEntity(em);
            em.persist(venta);
            em.flush();
        } else {
            venta = TestUtil.findAll(em, Venta.class).get(0);
        }
        updatedVentaAsiento.setVenta(venta);
        return updatedVentaAsiento;
    }

    @BeforeEach
    void initTest() {
        ventaAsiento = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedVentaAsiento != null) {
            ventaAsientoRepository.delete(insertedVentaAsiento);
            insertedVentaAsiento = null;
        }
    }

    @Test
    @Transactional
    void createVentaAsiento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VentaAsiento
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);
        var returnedVentaAsientoDTO = om.readValue(
            restVentaAsientoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaAsientoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VentaAsientoDTO.class
        );

        // Validate the VentaAsiento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVentaAsiento = ventaAsientoMapper.toEntity(returnedVentaAsientoDTO);
        assertVentaAsientoUpdatableFieldsEquals(returnedVentaAsiento, getPersistedVentaAsiento(returnedVentaAsiento));

        insertedVentaAsiento = returnedVentaAsiento;
    }

    @Test
    @Transactional
    void createVentaAsientoWithExistingId() throws Exception {
        // Create the VentaAsiento with an existing ID
        ventaAsiento.setId(1L);
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVentaAsientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaAsientoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFilaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventaAsiento.setFila(null);

        // Create the VentaAsiento, which fails.
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        restVentaAsientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaAsientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkColumnaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventaAsiento.setColumna(null);

        // Create the VentaAsiento, which fails.
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        restVentaAsientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaAsientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPersonaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventaAsiento.setPersona(null);

        // Create the VentaAsiento, which fails.
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        restVentaAsientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaAsientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventaAsiento.setEstado(null);

        // Create the VentaAsiento, which fails.
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        restVentaAsientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaAsientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVentaAsientos() throws Exception {
        // Initialize the database
        insertedVentaAsiento = ventaAsientoRepository.saveAndFlush(ventaAsiento);

        // Get all the ventaAsientoList
        restVentaAsientoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ventaAsiento.getId().intValue())))
            .andExpect(jsonPath("$.[*].fila").value(hasItem(DEFAULT_FILA)))
            .andExpect(jsonPath("$.[*].columna").value(hasItem(DEFAULT_COLUMNA)))
            .andExpect(jsonPath("$.[*].persona").value(hasItem(DEFAULT_PERSONA)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO)));
    }

    @Test
    @Transactional
    void getVentaAsiento() throws Exception {
        // Initialize the database
        insertedVentaAsiento = ventaAsientoRepository.saveAndFlush(ventaAsiento);

        // Get the ventaAsiento
        restVentaAsientoMockMvc
            .perform(get(ENTITY_API_URL_ID, ventaAsiento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ventaAsiento.getId().intValue()))
            .andExpect(jsonPath("$.fila").value(DEFAULT_FILA))
            .andExpect(jsonPath("$.columna").value(DEFAULT_COLUMNA))
            .andExpect(jsonPath("$.persona").value(DEFAULT_PERSONA))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO));
    }

    @Test
    @Transactional
    void getNonExistingVentaAsiento() throws Exception {
        // Get the ventaAsiento
        restVentaAsientoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVentaAsiento() throws Exception {
        // Initialize the database
        insertedVentaAsiento = ventaAsientoRepository.saveAndFlush(ventaAsiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ventaAsiento
        VentaAsiento updatedVentaAsiento = ventaAsientoRepository.findById(ventaAsiento.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVentaAsiento are not directly saved in db
        em.detach(updatedVentaAsiento);
        updatedVentaAsiento.fila(UPDATED_FILA).columna(UPDATED_COLUMNA).persona(UPDATED_PERSONA).estado(UPDATED_ESTADO);
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(updatedVentaAsiento);

        restVentaAsientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventaAsientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventaAsientoDTO))
            )
            .andExpect(status().isOk());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVentaAsientoToMatchAllProperties(updatedVentaAsiento);
    }

    @Test
    @Transactional
    void putNonExistingVentaAsiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventaAsiento.setId(longCount.incrementAndGet());

        // Create the VentaAsiento
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentaAsientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventaAsientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventaAsientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVentaAsiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventaAsiento.setId(longCount.incrementAndGet());

        // Create the VentaAsiento
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaAsientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventaAsientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVentaAsiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventaAsiento.setId(longCount.incrementAndGet());

        // Create the VentaAsiento
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaAsientoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaAsientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVentaAsientoWithPatch() throws Exception {
        // Initialize the database
        insertedVentaAsiento = ventaAsientoRepository.saveAndFlush(ventaAsiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ventaAsiento using partial update
        VentaAsiento partialUpdatedVentaAsiento = new VentaAsiento();
        partialUpdatedVentaAsiento.setId(ventaAsiento.getId());

        partialUpdatedVentaAsiento.fila(UPDATED_FILA);

        restVentaAsientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVentaAsiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVentaAsiento))
            )
            .andExpect(status().isOk());

        // Validate the VentaAsiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentaAsientoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVentaAsiento, ventaAsiento),
            getPersistedVentaAsiento(ventaAsiento)
        );
    }

    @Test
    @Transactional
    void fullUpdateVentaAsientoWithPatch() throws Exception {
        // Initialize the database
        insertedVentaAsiento = ventaAsientoRepository.saveAndFlush(ventaAsiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ventaAsiento using partial update
        VentaAsiento partialUpdatedVentaAsiento = new VentaAsiento();
        partialUpdatedVentaAsiento.setId(ventaAsiento.getId());

        partialUpdatedVentaAsiento.fila(UPDATED_FILA).columna(UPDATED_COLUMNA).persona(UPDATED_PERSONA).estado(UPDATED_ESTADO);

        restVentaAsientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVentaAsiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVentaAsiento))
            )
            .andExpect(status().isOk());

        // Validate the VentaAsiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentaAsientoUpdatableFieldsEquals(partialUpdatedVentaAsiento, getPersistedVentaAsiento(partialUpdatedVentaAsiento));
    }

    @Test
    @Transactional
    void patchNonExistingVentaAsiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventaAsiento.setId(longCount.incrementAndGet());

        // Create the VentaAsiento
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentaAsientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ventaAsientoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventaAsientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVentaAsiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventaAsiento.setId(longCount.incrementAndGet());

        // Create the VentaAsiento
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaAsientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventaAsientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVentaAsiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventaAsiento.setId(longCount.incrementAndGet());

        // Create the VentaAsiento
        VentaAsientoDTO ventaAsientoDTO = ventaAsientoMapper.toDto(ventaAsiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaAsientoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ventaAsientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VentaAsiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVentaAsiento() throws Exception {
        // Initialize the database
        insertedVentaAsiento = ventaAsientoRepository.saveAndFlush(ventaAsiento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ventaAsiento
        restVentaAsientoMockMvc
            .perform(delete(ENTITY_API_URL_ID, ventaAsiento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ventaAsientoRepository.count();
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

    protected VentaAsiento getPersistedVentaAsiento(VentaAsiento ventaAsiento) {
        return ventaAsientoRepository.findById(ventaAsiento.getId()).orElseThrow();
    }

    protected void assertPersistedVentaAsientoToMatchAllProperties(VentaAsiento expectedVentaAsiento) {
        assertVentaAsientoAllPropertiesEquals(expectedVentaAsiento, getPersistedVentaAsiento(expectedVentaAsiento));
    }

    protected void assertPersistedVentaAsientoToMatchUpdatableProperties(VentaAsiento expectedVentaAsiento) {
        assertVentaAsientoAllUpdatablePropertiesEquals(expectedVentaAsiento, getPersistedVentaAsiento(expectedVentaAsiento));
    }
}
