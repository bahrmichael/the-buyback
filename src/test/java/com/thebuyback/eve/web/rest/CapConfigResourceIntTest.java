package com.thebuyback.eve.web.rest;

import com.thebuyback.eve.TheBuybackApp;

import com.thebuyback.eve.domain.CapConfig;
import com.thebuyback.eve.repository.CapConfigRepository;
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

/**
 * Test class for the CapConfigResource REST controller.
 *
 * @see CapConfigResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TheBuybackApp.class)
public class CapConfigResourceIntTest {

    private static final Integer DEFAULT_TYPE_ID = 1;
    private static final Integer UPDATED_TYPE_ID = 2;

    private static final String DEFAULT_TYPE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final String DEFAULT_DELIVERY_LOCATION_1 = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_LOCATION_1 = "BBBBBBBBBB";

    private static final Double DEFAULT_DELIVERY_PRICE_1 = 1D;
    private static final Double UPDATED_DELIVERY_PRICE_1 = 2D;

    private static final String DEFAULT_DELIVERY_LOCATION_2 = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_LOCATION_2 = "BBBBBBBBBB";

    private static final Double DEFAULT_DELIVERY_PRICE_2 = 1D;
    private static final Double UPDATED_DELIVERY_PRICE_2 = 2D;

    private static final String DEFAULT_DELIVERY_LOCATION_3 = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_LOCATION_3 = "BBBBBBBBBB";

    private static final Double DEFAULT_DELIVERY_PRICE_3 = 1D;
    private static final Double UPDATED_DELIVERY_PRICE_3 = 2D;

    private static final String DEFAULT_DELIVERY_LOCATION_4 = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_LOCATION_4 = "BBBBBBBBBB";

    private static final Double DEFAULT_DELIVERY_PRICE_4 = 1D;
    private static final Double UPDATED_DELIVERY_PRICE_4 = 2D;

    @Autowired
    private CapConfigRepository capConfigRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restCapConfigMockMvc;

    private CapConfig capConfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CapConfigResource capConfigResource = new CapConfigResource(capConfigRepository);
        this.restCapConfigMockMvc = MockMvcBuilders.standaloneSetup(capConfigResource)
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
    public static CapConfig createEntity() {
        CapConfig capConfig = new CapConfig()
            .typeId(DEFAULT_TYPE_ID)
            .typeName(DEFAULT_TYPE_NAME)
            .price(DEFAULT_PRICE)
            .deliveryLocation1(DEFAULT_DELIVERY_LOCATION_1)
            .deliveryPrice1(DEFAULT_DELIVERY_PRICE_1)
            .deliveryLocation2(DEFAULT_DELIVERY_LOCATION_2)
            .deliveryPrice2(DEFAULT_DELIVERY_PRICE_2)
            .deliveryLocation3(DEFAULT_DELIVERY_LOCATION_3)
            .deliveryPrice3(DEFAULT_DELIVERY_PRICE_3)
            .deliveryLocation4(DEFAULT_DELIVERY_LOCATION_4)
            .deliveryPrice4(DEFAULT_DELIVERY_PRICE_4);
        return capConfig;
    }

    @Before
    public void initTest() {
        capConfigRepository.deleteAll();
        capConfig = createEntity();
    }

    @Test
    public void createCapConfig() throws Exception {
        int databaseSizeBeforeCreate = capConfigRepository.findAll().size();

        // Create the CapConfig
        restCapConfigMockMvc.perform(post("/api/cap-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capConfig)))
            .andExpect(status().isCreated());

        // Validate the CapConfig in the database
        List<CapConfig> capConfigList = capConfigRepository.findAll();
        assertThat(capConfigList).hasSize(databaseSizeBeforeCreate + 1);
        CapConfig testCapConfig = capConfigList.get(capConfigList.size() - 1);
        assertThat(testCapConfig.getTypeId()).isEqualTo(DEFAULT_TYPE_ID);
        assertThat(testCapConfig.getTypeName()).isEqualTo(DEFAULT_TYPE_NAME);
        assertThat(testCapConfig.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testCapConfig.getDeliveryLocation1()).isEqualTo(DEFAULT_DELIVERY_LOCATION_1);
        assertThat(testCapConfig.getDeliveryPrice1()).isEqualTo(DEFAULT_DELIVERY_PRICE_1);
        assertThat(testCapConfig.getDeliveryLocation2()).isEqualTo(DEFAULT_DELIVERY_LOCATION_2);
        assertThat(testCapConfig.getDeliveryPrice2()).isEqualTo(DEFAULT_DELIVERY_PRICE_2);
        assertThat(testCapConfig.getDeliveryLocation3()).isEqualTo(DEFAULT_DELIVERY_LOCATION_3);
        assertThat(testCapConfig.getDeliveryPrice3()).isEqualTo(DEFAULT_DELIVERY_PRICE_3);
        assertThat(testCapConfig.getDeliveryLocation4()).isEqualTo(DEFAULT_DELIVERY_LOCATION_4);
        assertThat(testCapConfig.getDeliveryPrice4()).isEqualTo(DEFAULT_DELIVERY_PRICE_4);
    }

    @Test
    public void createCapConfigWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = capConfigRepository.findAll().size();

        // Create the CapConfig with an existing ID
        capConfig.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapConfigMockMvc.perform(post("/api/cap-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capConfig)))
            .andExpect(status().isBadRequest());

        // Validate the CapConfig in the database
        List<CapConfig> capConfigList = capConfigRepository.findAll();
        assertThat(capConfigList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllCapConfigs() throws Exception {
        // Initialize the database
        capConfigRepository.save(capConfig);

        // Get all the capConfigList
        restCapConfigMockMvc.perform(get("/api/cap-configs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capConfig.getId())))
            .andExpect(jsonPath("$.[*].typeId").value(hasItem(DEFAULT_TYPE_ID)))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].deliveryLocation1").value(hasItem(DEFAULT_DELIVERY_LOCATION_1.toString())))
            .andExpect(jsonPath("$.[*].deliveryPrice1").value(hasItem(DEFAULT_DELIVERY_PRICE_1.doubleValue())))
            .andExpect(jsonPath("$.[*].deliveryLocation2").value(hasItem(DEFAULT_DELIVERY_LOCATION_2.toString())))
            .andExpect(jsonPath("$.[*].deliveryPrice2").value(hasItem(DEFAULT_DELIVERY_PRICE_2.doubleValue())))
            .andExpect(jsonPath("$.[*].deliveryLocation3").value(hasItem(DEFAULT_DELIVERY_LOCATION_3.toString())))
            .andExpect(jsonPath("$.[*].deliveryPrice3").value(hasItem(DEFAULT_DELIVERY_PRICE_3.doubleValue())))
            .andExpect(jsonPath("$.[*].deliveryLocation4").value(hasItem(DEFAULT_DELIVERY_LOCATION_4.toString())))
            .andExpect(jsonPath("$.[*].deliveryPrice4").value(hasItem(DEFAULT_DELIVERY_PRICE_4.doubleValue())));
    }

    @Test
    public void getCapConfig() throws Exception {
        // Initialize the database
        capConfigRepository.save(capConfig);

        // Get the capConfig
        restCapConfigMockMvc.perform(get("/api/cap-configs/{id}", capConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(capConfig.getId()))
            .andExpect(jsonPath("$.typeId").value(DEFAULT_TYPE_ID))
            .andExpect(jsonPath("$.typeName").value(DEFAULT_TYPE_NAME.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.deliveryLocation1").value(DEFAULT_DELIVERY_LOCATION_1.toString()))
            .andExpect(jsonPath("$.deliveryPrice1").value(DEFAULT_DELIVERY_PRICE_1.doubleValue()))
            .andExpect(jsonPath("$.deliveryLocation2").value(DEFAULT_DELIVERY_LOCATION_2.toString()))
            .andExpect(jsonPath("$.deliveryPrice2").value(DEFAULT_DELIVERY_PRICE_2.doubleValue()))
            .andExpect(jsonPath("$.deliveryLocation3").value(DEFAULT_DELIVERY_LOCATION_3.toString()))
            .andExpect(jsonPath("$.deliveryPrice3").value(DEFAULT_DELIVERY_PRICE_3.doubleValue()))
            .andExpect(jsonPath("$.deliveryLocation4").value(DEFAULT_DELIVERY_LOCATION_4.toString()))
            .andExpect(jsonPath("$.deliveryPrice4").value(DEFAULT_DELIVERY_PRICE_4.doubleValue()));
    }

    @Test
    public void getNonExistingCapConfig() throws Exception {
        // Get the capConfig
        restCapConfigMockMvc.perform(get("/api/cap-configs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCapConfig() throws Exception {
        // Initialize the database
        capConfigRepository.save(capConfig);
        int databaseSizeBeforeUpdate = capConfigRepository.findAll().size();

        // Update the capConfig
        CapConfig updatedCapConfig = capConfigRepository.findOne(capConfig.getId());
        updatedCapConfig
            .typeId(UPDATED_TYPE_ID)
            .typeName(UPDATED_TYPE_NAME)
            .price(UPDATED_PRICE)
            .deliveryLocation1(UPDATED_DELIVERY_LOCATION_1)
            .deliveryPrice1(UPDATED_DELIVERY_PRICE_1)
            .deliveryLocation2(UPDATED_DELIVERY_LOCATION_2)
            .deliveryPrice2(UPDATED_DELIVERY_PRICE_2)
            .deliveryLocation3(UPDATED_DELIVERY_LOCATION_3)
            .deliveryPrice3(UPDATED_DELIVERY_PRICE_3)
            .deliveryLocation4(UPDATED_DELIVERY_LOCATION_4)
            .deliveryPrice4(UPDATED_DELIVERY_PRICE_4);

        restCapConfigMockMvc.perform(put("/api/cap-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCapConfig)))
            .andExpect(status().isOk());

        // Validate the CapConfig in the database
        List<CapConfig> capConfigList = capConfigRepository.findAll();
        assertThat(capConfigList).hasSize(databaseSizeBeforeUpdate);
        CapConfig testCapConfig = capConfigList.get(capConfigList.size() - 1);
        assertThat(testCapConfig.getTypeId()).isEqualTo(UPDATED_TYPE_ID);
        assertThat(testCapConfig.getTypeName()).isEqualTo(UPDATED_TYPE_NAME);
        assertThat(testCapConfig.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCapConfig.getDeliveryLocation1()).isEqualTo(UPDATED_DELIVERY_LOCATION_1);
        assertThat(testCapConfig.getDeliveryPrice1()).isEqualTo(UPDATED_DELIVERY_PRICE_1);
        assertThat(testCapConfig.getDeliveryLocation2()).isEqualTo(UPDATED_DELIVERY_LOCATION_2);
        assertThat(testCapConfig.getDeliveryPrice2()).isEqualTo(UPDATED_DELIVERY_PRICE_2);
        assertThat(testCapConfig.getDeliveryLocation3()).isEqualTo(UPDATED_DELIVERY_LOCATION_3);
        assertThat(testCapConfig.getDeliveryPrice3()).isEqualTo(UPDATED_DELIVERY_PRICE_3);
        assertThat(testCapConfig.getDeliveryLocation4()).isEqualTo(UPDATED_DELIVERY_LOCATION_4);
        assertThat(testCapConfig.getDeliveryPrice4()).isEqualTo(UPDATED_DELIVERY_PRICE_4);
    }

    @Test
    public void updateNonExistingCapConfig() throws Exception {
        int databaseSizeBeforeUpdate = capConfigRepository.findAll().size();

        // Create the CapConfig

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCapConfigMockMvc.perform(put("/api/cap-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capConfig)))
            .andExpect(status().isCreated());

        // Validate the CapConfig in the database
        List<CapConfig> capConfigList = capConfigRepository.findAll();
        assertThat(capConfigList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteCapConfig() throws Exception {
        // Initialize the database
        capConfigRepository.save(capConfig);
        int databaseSizeBeforeDelete = capConfigRepository.findAll().size();

        // Get the capConfig
        restCapConfigMockMvc.perform(delete("/api/cap-configs/{id}", capConfig.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CapConfig> capConfigList = capConfigRepository.findAll();
        assertThat(capConfigList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CapConfig.class);
        CapConfig capConfig1 = new CapConfig();
        capConfig1.setId("id1");
        CapConfig capConfig2 = new CapConfig();
        capConfig2.setId(capConfig1.getId());
        assertThat(capConfig1).isEqualTo(capConfig2);
        capConfig2.setId("id2");
        assertThat(capConfig1).isNotEqualTo(capConfig2);
        capConfig1.setId(null);
        assertThat(capConfig1).isNotEqualTo(capConfig2);
    }
}
