package com.thebuyback.eve.web.rest;

import com.thebuyback.eve.TheBuybackApp;

import com.thebuyback.eve.domain.TypeBuybackRate;
import com.thebuyback.eve.repository.TypeBuybackRateRepository;
import com.thebuyback.eve.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.thebuyback.eve.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thebuyback.eve.domain.enumeration.TypeCategory;
/**
 * Test class for the TypeBuybackRateResource REST controller.
 *
 * @see TypeBuybackRateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TheBuybackApp.class)
public class TypeBuybackRateResourceIntTest {

    private static final Long DEFAULT_TYPE_ID = 1L;
    private static final Long UPDATED_TYPE_ID = 2L;

    private static final String DEFAULT_TYPE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_NAME = "BBBBBBBBBB";

    private static final TypeCategory DEFAULT_CATEGORY = TypeCategory.COMPRESSED_ORE;
    private static final TypeCategory UPDATED_CATEGORY = TypeCategory.MOON_ORE;

    private static final Double DEFAULT_RATE = 1D;
    private static final Double UPDATED_RATE = 2D;

    @Autowired
    private TypeBuybackRateRepository typeBuybackRateRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restTypeBuybackRateMockMvc;

    private TypeBuybackRate typeBuybackRate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TypeBuybackRateResource typeBuybackRateResource = new TypeBuybackRateResource(typeBuybackRateRepository);
        this.restTypeBuybackRateMockMvc = MockMvcBuilders.standaloneSetup(typeBuybackRateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeBuybackRate createEntity() {
        TypeBuybackRate typeBuybackRate = new TypeBuybackRate()
            .typeId(DEFAULT_TYPE_ID)
            .typeName(DEFAULT_TYPE_NAME)
            .category(DEFAULT_CATEGORY)
            .rate(DEFAULT_RATE);
        return typeBuybackRate;
    }

    @Before
    public void initTest() {
        typeBuybackRateRepository.deleteAll();
        typeBuybackRate = createEntity();
    }

    @Test
    public void createTypeBuybackRate() throws Exception {
        int databaseSizeBeforeCreate = typeBuybackRateRepository.findAll().size();

        // Create the TypeBuybackRate
        restTypeBuybackRateMockMvc.perform(post("/api/type-buyback-rates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(typeBuybackRate)))
            .andExpect(status().isCreated());

        // Validate the TypeBuybackRate in the database
        List<TypeBuybackRate> typeBuybackRateList = typeBuybackRateRepository.findAll();
        assertThat(typeBuybackRateList).hasSize(databaseSizeBeforeCreate + 1);
        TypeBuybackRate testTypeBuybackRate = typeBuybackRateList.get(typeBuybackRateList.size() - 1);
        assertThat(testTypeBuybackRate.getTypeId()).isEqualTo(DEFAULT_TYPE_ID);
        assertThat(testTypeBuybackRate.getTypeName()).isEqualTo(DEFAULT_TYPE_NAME);
        assertThat(testTypeBuybackRate.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testTypeBuybackRate.getRate()).isEqualTo(DEFAULT_RATE);
    }

    @Test
    public void createTypeBuybackRateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = typeBuybackRateRepository.findAll().size();

        // Create the TypeBuybackRate with an existing ID
        typeBuybackRate.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeBuybackRateMockMvc.perform(post("/api/type-buyback-rates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(typeBuybackRate)))
            .andExpect(status().isBadRequest());

        // Validate the TypeBuybackRate in the database
        List<TypeBuybackRate> typeBuybackRateList = typeBuybackRateRepository.findAll();
        assertThat(typeBuybackRateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllTypeBuybackRates() throws Exception {
        // Initialize the database
        typeBuybackRateRepository.save(typeBuybackRate);

        // Get all the typeBuybackRateList
        restTypeBuybackRateMockMvc.perform(get("/api/type-buyback-rates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeBuybackRate.getId())))
            .andExpect(jsonPath("$.[*].typeId").value(hasItem(DEFAULT_TYPE_ID.intValue())))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME.toString())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(DEFAULT_RATE.doubleValue())));
    }

    @Test
    public void getTypeBuybackRate() throws Exception {
        // Initialize the database
        typeBuybackRateRepository.save(typeBuybackRate);

        // Get the typeBuybackRate
        restTypeBuybackRateMockMvc.perform(get("/api/type-buyback-rates/{id}", typeBuybackRate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(typeBuybackRate.getId()))
            .andExpect(jsonPath("$.typeId").value(DEFAULT_TYPE_ID.intValue()))
            .andExpect(jsonPath("$.typeName").value(DEFAULT_TYPE_NAME.toString()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.rate").value(DEFAULT_RATE.doubleValue()));
    }

    @Test
    public void getNonExistingTypeBuybackRate() throws Exception {
        // Get the typeBuybackRate
        restTypeBuybackRateMockMvc.perform(get("/api/type-buyback-rates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateTypeBuybackRate() throws Exception {
        // Initialize the database
        typeBuybackRateRepository.save(typeBuybackRate);
        int databaseSizeBeforeUpdate = typeBuybackRateRepository.findAll().size();

        // Update the typeBuybackRate
        TypeBuybackRate updatedTypeBuybackRate = typeBuybackRateRepository.findOne(typeBuybackRate.getId());
        updatedTypeBuybackRate
            .typeId(UPDATED_TYPE_ID)
            .typeName(UPDATED_TYPE_NAME)
            .category(UPDATED_CATEGORY)
            .rate(UPDATED_RATE);

        restTypeBuybackRateMockMvc.perform(put("/api/type-buyback-rates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTypeBuybackRate)))
            .andExpect(status().isOk());

        // Validate the TypeBuybackRate in the database
        List<TypeBuybackRate> typeBuybackRateList = typeBuybackRateRepository.findAll();
        assertThat(typeBuybackRateList).hasSize(databaseSizeBeforeUpdate);
        TypeBuybackRate testTypeBuybackRate = typeBuybackRateList.get(typeBuybackRateList.size() - 1);
        assertThat(testTypeBuybackRate.getTypeId()).isEqualTo(UPDATED_TYPE_ID);
        assertThat(testTypeBuybackRate.getTypeName()).isEqualTo(UPDATED_TYPE_NAME);
        assertThat(testTypeBuybackRate.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testTypeBuybackRate.getRate()).isEqualTo(UPDATED_RATE);
    }

    @Test
    public void updateNonExistingTypeBuybackRate() throws Exception {
        int databaseSizeBeforeUpdate = typeBuybackRateRepository.findAll().size();

        // Create the TypeBuybackRate

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTypeBuybackRateMockMvc.perform(put("/api/type-buyback-rates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(typeBuybackRate)))
            .andExpect(status().isCreated());

        // Validate the TypeBuybackRate in the database
        List<TypeBuybackRate> typeBuybackRateList = typeBuybackRateRepository.findAll();
        assertThat(typeBuybackRateList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteTypeBuybackRate() throws Exception {
        // Initialize the database
        typeBuybackRateRepository.save(typeBuybackRate);
        int databaseSizeBeforeDelete = typeBuybackRateRepository.findAll().size();

        // Get the typeBuybackRate
        restTypeBuybackRateMockMvc.perform(delete("/api/type-buyback-rates/{id}", typeBuybackRate.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TypeBuybackRate> typeBuybackRateList = typeBuybackRateRepository.findAll();
        assertThat(typeBuybackRateList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TypeBuybackRate.class);
        TypeBuybackRate typeBuybackRate1 = new TypeBuybackRate();
        typeBuybackRate1.setId("id1");
        TypeBuybackRate typeBuybackRate2 = new TypeBuybackRate();
        typeBuybackRate2.setId(typeBuybackRate1.getId());
        assertThat(typeBuybackRate1).isEqualTo(typeBuybackRate2);
        typeBuybackRate2.setId("id2");
        assertThat(typeBuybackRate1).isNotEqualTo(typeBuybackRate2);
        typeBuybackRate1.setId(null);
        assertThat(typeBuybackRate1).isNotEqualTo(typeBuybackRate2);
    }
}
