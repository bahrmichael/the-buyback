package com.thebuyback.eve.web.rest;

import com.thebuyback.eve.TheBuybackApp;

import com.thebuyback.eve.domain.ManufacturingOrder;
import com.thebuyback.eve.repository.ManufacturingOrderRepository;
import com.thebuyback.eve.service.ManufacturingOrderService;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.thebuyback.eve.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thebuyback.eve.domain.enumeration.ManufacturingOrderStatus;
/**
 * Test class for the ManufacturingOrderResource REST controller.
 *
 * @see ManufacturingOrderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TheBuybackApp.class)
public class ManufacturingOrderResourceIntTest {

    private static final Double DEFAULT_PRICE_PER_UNIT = 1D;
    private static final Double UPDATED_PRICE_PER_UNIT = 2D;

    private static final String DEFAULT_TYPE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final Instant DEFAULT_INSTANT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INSTANT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ASSIGNEE = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNEE = "BBBBBBBBBB";

    private static final ManufacturingOrderStatus DEFAULT_STATUS = ManufacturingOrderStatus.OPEN;
    private static final ManufacturingOrderStatus UPDATED_STATUS = ManufacturingOrderStatus.IN_PROGRESS;

    @Autowired
    private ManufacturingOrderRepository manufacturingOrderRepository;

    @Autowired
    private ManufacturingOrderService manufacturingOrderService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restManufacturingOrderMockMvc;

    private ManufacturingOrder manufacturingOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ManufacturingOrderResource manufacturingOrderResource = new ManufacturingOrderResource(manufacturingOrderService);
        this.restManufacturingOrderMockMvc = MockMvcBuilders.standaloneSetup(manufacturingOrderResource)
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
    public static ManufacturingOrder createEntity() {
        ManufacturingOrder manufacturingOrder = new ManufacturingOrder()
            .pricePerUnit(DEFAULT_PRICE_PER_UNIT)
            .typeName(DEFAULT_TYPE_NAME)
            .amount(DEFAULT_AMOUNT)
            .instant(DEFAULT_INSTANT)
            .assignee(DEFAULT_ASSIGNEE)
            .status(DEFAULT_STATUS);
        return manufacturingOrder;
    }

    @Before
    public void initTest() {
        manufacturingOrderRepository.deleteAll();
        manufacturingOrder = createEntity();
    }

    @Test
    public void createManufacturingOrder() throws Exception {
        int databaseSizeBeforeCreate = manufacturingOrderRepository.findAll().size();

        // Create the ManufacturingOrder
        restManufacturingOrderMockMvc.perform(post("/api/manufacturing-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(manufacturingOrder)))
            .andExpect(status().isCreated());

        // Validate the ManufacturingOrder in the database
        List<ManufacturingOrder> manufacturingOrderList = manufacturingOrderRepository.findAll();
        assertThat(manufacturingOrderList).hasSize(databaseSizeBeforeCreate + 1);
        ManufacturingOrder testManufacturingOrder = manufacturingOrderList.get(manufacturingOrderList.size() - 1);
        assertThat(testManufacturingOrder.getPricePerUnit()).isEqualTo(DEFAULT_PRICE_PER_UNIT);
        assertThat(testManufacturingOrder.getTypeName()).isEqualTo(DEFAULT_TYPE_NAME);
        assertThat(testManufacturingOrder.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testManufacturingOrder.getInstant()).isEqualTo(DEFAULT_INSTANT);
        assertThat(testManufacturingOrder.getAssignee()).isEqualTo(DEFAULT_ASSIGNEE);
        assertThat(testManufacturingOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    public void createManufacturingOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = manufacturingOrderRepository.findAll().size();

        // Create the ManufacturingOrder with an existing ID
        manufacturingOrder.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restManufacturingOrderMockMvc.perform(post("/api/manufacturing-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(manufacturingOrder)))
            .andExpect(status().isBadRequest());

        // Validate the ManufacturingOrder in the database
        List<ManufacturingOrder> manufacturingOrderList = manufacturingOrderRepository.findAll();
        assertThat(manufacturingOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllManufacturingOrders() throws Exception {
        // Initialize the database
        manufacturingOrderRepository.save(manufacturingOrder);

        // Get all the manufacturingOrderList
        restManufacturingOrderMockMvc.perform(get("/api/manufacturing-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(manufacturingOrder.getId())))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(DEFAULT_PRICE_PER_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].instant").value(hasItem(DEFAULT_INSTANT.toString())))
            .andExpect(jsonPath("$.[*].assignee").value(hasItem(DEFAULT_ASSIGNEE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    public void getManufacturingOrder() throws Exception {
        // Initialize the database
        manufacturingOrderRepository.save(manufacturingOrder);

        // Get the manufacturingOrder
        restManufacturingOrderMockMvc.perform(get("/api/manufacturing-orders/{id}", manufacturingOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(manufacturingOrder.getId()))
            .andExpect(jsonPath("$.pricePerUnit").value(DEFAULT_PRICE_PER_UNIT.doubleValue()))
            .andExpect(jsonPath("$.typeName").value(DEFAULT_TYPE_NAME.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
            .andExpect(jsonPath("$.instant").value(DEFAULT_INSTANT.toString()))
            .andExpect(jsonPath("$.assignee").value(DEFAULT_ASSIGNEE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    public void getNonExistingManufacturingOrder() throws Exception {
        // Get the manufacturingOrder
        restManufacturingOrderMockMvc.perform(get("/api/manufacturing-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateManufacturingOrder() throws Exception {
        // Initialize the database
        manufacturingOrderService.save(manufacturingOrder);

        int databaseSizeBeforeUpdate = manufacturingOrderRepository.findAll().size();

        // Update the manufacturingOrder
        ManufacturingOrder updatedManufacturingOrder = manufacturingOrderRepository.findOne(manufacturingOrder.getId());
        updatedManufacturingOrder
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .typeName(UPDATED_TYPE_NAME)
            .amount(UPDATED_AMOUNT)
            .instant(UPDATED_INSTANT)
            .assignee(UPDATED_ASSIGNEE)
            .status(UPDATED_STATUS);

        restManufacturingOrderMockMvc.perform(put("/api/manufacturing-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedManufacturingOrder)))
            .andExpect(status().isOk());

        // Validate the ManufacturingOrder in the database
        List<ManufacturingOrder> manufacturingOrderList = manufacturingOrderRepository.findAll();
        assertThat(manufacturingOrderList).hasSize(databaseSizeBeforeUpdate);
        ManufacturingOrder testManufacturingOrder = manufacturingOrderList.get(manufacturingOrderList.size() - 1);
        assertThat(testManufacturingOrder.getPricePerUnit()).isEqualTo(UPDATED_PRICE_PER_UNIT);
        assertThat(testManufacturingOrder.getTypeName()).isEqualTo(UPDATED_TYPE_NAME);
        assertThat(testManufacturingOrder.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testManufacturingOrder.getInstant()).isEqualTo(UPDATED_INSTANT);
        assertThat(testManufacturingOrder.getAssignee()).isEqualTo(UPDATED_ASSIGNEE);
        assertThat(testManufacturingOrder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    public void updateNonExistingManufacturingOrder() throws Exception {
        int databaseSizeBeforeUpdate = manufacturingOrderRepository.findAll().size();

        // Create the ManufacturingOrder

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restManufacturingOrderMockMvc.perform(put("/api/manufacturing-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(manufacturingOrder)))
            .andExpect(status().isCreated());

        // Validate the ManufacturingOrder in the database
        List<ManufacturingOrder> manufacturingOrderList = manufacturingOrderRepository.findAll();
        assertThat(manufacturingOrderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteManufacturingOrder() throws Exception {
        // Initialize the database
        manufacturingOrderService.save(manufacturingOrder);

        int databaseSizeBeforeDelete = manufacturingOrderRepository.findAll().size();

        // Get the manufacturingOrder
        restManufacturingOrderMockMvc.perform(delete("/api/manufacturing-orders/{id}", manufacturingOrder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ManufacturingOrder> manufacturingOrderList = manufacturingOrderRepository.findAll();
        assertThat(manufacturingOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ManufacturingOrder.class);
        ManufacturingOrder manufacturingOrder1 = new ManufacturingOrder();
        manufacturingOrder1.setId("id1");
        ManufacturingOrder manufacturingOrder2 = new ManufacturingOrder();
        manufacturingOrder2.setId(manufacturingOrder1.getId());
        assertThat(manufacturingOrder1).isEqualTo(manufacturingOrder2);
        manufacturingOrder2.setId("id2");
        assertThat(manufacturingOrder1).isNotEqualTo(manufacturingOrder2);
        manufacturingOrder1.setId(null);
        assertThat(manufacturingOrder1).isNotEqualTo(manufacturingOrder2);
    }
}
