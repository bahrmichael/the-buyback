package com.thebuyback.eve.web.rest;

import com.thebuyback.eve.TheBuybackApp;

import com.thebuyback.eve.domain.CapOrder;
import com.thebuyback.eve.repository.CapOrderRepository;
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

import com.thebuyback.eve.domain.enumeration.CapOrderStatus;
/**
 * Test class for the CapOrderResource REST controller.
 *
 * @see CapOrderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TheBuybackApp.class)
public class CapOrderResourceIntTest {

    private static final String DEFAULT_RECIPIENT = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_TYPE_ID = 1;
    private static final Integer UPDATED_TYPE_ID = 2;

    private static final String DEFAULT_TYPE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DELIVERY_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_LOCATION = "BBBBBBBBBB";

    private static final Double DEFAULT_DELIVERY_PRICE = 1D;
    private static final Double UPDATED_DELIVERY_PRICE = 2D;

    private static final CapOrderStatus DEFAULT_STATUS = CapOrderStatus.REQUESTED;
    private static final CapOrderStatus UPDATED_STATUS = CapOrderStatus.INBUILD;

    @Autowired
    private CapOrderRepository capOrderRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restCapOrderMockMvc;

    private CapOrder capOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CapOrderResource capOrderResource = new CapOrderResource(capOrderRepository);
        this.restCapOrderMockMvc = MockMvcBuilders.standaloneSetup(capOrderResource)
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
    public static CapOrder createEntity() {
        CapOrder capOrder = new CapOrder()
            .recipient(DEFAULT_RECIPIENT)
            .typeId(DEFAULT_TYPE_ID)
            .typeName(DEFAULT_TYPE_NAME)
            .price(DEFAULT_PRICE)
            .created(DEFAULT_CREATED)
            .deliveryLocation(DEFAULT_DELIVERY_LOCATION)
            .deliveryPrice(DEFAULT_DELIVERY_PRICE)
            .status(DEFAULT_STATUS);
        return capOrder;
    }

    @Before
    public void initTest() {
        capOrderRepository.deleteAll();
        capOrder = createEntity();
    }

    @Test
    public void createCapOrder() throws Exception {
        int databaseSizeBeforeCreate = capOrderRepository.findAll().size();

        // Create the CapOrder
        restCapOrderMockMvc.perform(post("/api/cap-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capOrder)))
            .andExpect(status().isCreated());

        // Validate the CapOrder in the database
        List<CapOrder> capOrderList = capOrderRepository.findAll();
        assertThat(capOrderList).hasSize(databaseSizeBeforeCreate + 1);
        CapOrder testCapOrder = capOrderList.get(capOrderList.size() - 1);
        assertThat(testCapOrder.getRecipient()).isEqualTo(DEFAULT_RECIPIENT);
        assertThat(testCapOrder.getTypeId()).isEqualTo(DEFAULT_TYPE_ID);
        assertThat(testCapOrder.getTypeName()).isEqualTo(DEFAULT_TYPE_NAME);
        assertThat(testCapOrder.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testCapOrder.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testCapOrder.getDeliveryLocation()).isEqualTo(DEFAULT_DELIVERY_LOCATION);
        assertThat(testCapOrder.getDeliveryPrice()).isEqualTo(DEFAULT_DELIVERY_PRICE);
        assertThat(testCapOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    public void createCapOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = capOrderRepository.findAll().size();

        // Create the CapOrder with an existing ID
        capOrder.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapOrderMockMvc.perform(post("/api/cap-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capOrder)))
            .andExpect(status().isBadRequest());

        // Validate the CapOrder in the database
        List<CapOrder> capOrderList = capOrderRepository.findAll();
        assertThat(capOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllCapOrders() throws Exception {
        // Initialize the database
        capOrderRepository.save(capOrder);

        // Get all the capOrderList
        restCapOrderMockMvc.perform(get("/api/cap-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capOrder.getId())))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT.toString())))
            .andExpect(jsonPath("$.[*].typeId").value(hasItem(DEFAULT_TYPE_ID)))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].deliveryLocation").value(hasItem(DEFAULT_DELIVERY_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].deliveryPrice").value(hasItem(DEFAULT_DELIVERY_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    public void getCapOrder() throws Exception {
        // Initialize the database
        capOrderRepository.save(capOrder);

        // Get the capOrder
        restCapOrderMockMvc.perform(get("/api/cap-orders/{id}", capOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(capOrder.getId()))
            .andExpect(jsonPath("$.recipient").value(DEFAULT_RECIPIENT.toString()))
            .andExpect(jsonPath("$.typeId").value(DEFAULT_TYPE_ID))
            .andExpect(jsonPath("$.typeName").value(DEFAULT_TYPE_NAME.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.deliveryLocation").value(DEFAULT_DELIVERY_LOCATION.toString()))
            .andExpect(jsonPath("$.deliveryPrice").value(DEFAULT_DELIVERY_PRICE.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    public void getNonExistingCapOrder() throws Exception {
        // Get the capOrder
        restCapOrderMockMvc.perform(get("/api/cap-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCapOrder() throws Exception {
        // Initialize the database
        capOrderRepository.save(capOrder);
        int databaseSizeBeforeUpdate = capOrderRepository.findAll().size();

        // Update the capOrder
        CapOrder updatedCapOrder = capOrderRepository.findOne(capOrder.getId());
        updatedCapOrder
            .recipient(UPDATED_RECIPIENT)
            .typeId(UPDATED_TYPE_ID)
            .typeName(UPDATED_TYPE_NAME)
            .price(UPDATED_PRICE)
            .created(UPDATED_CREATED)
            .deliveryLocation(UPDATED_DELIVERY_LOCATION)
            .deliveryPrice(UPDATED_DELIVERY_PRICE)
            .status(UPDATED_STATUS);

        restCapOrderMockMvc.perform(put("/api/cap-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCapOrder)))
            .andExpect(status().isOk());

        // Validate the CapOrder in the database
        List<CapOrder> capOrderList = capOrderRepository.findAll();
        assertThat(capOrderList).hasSize(databaseSizeBeforeUpdate);
        CapOrder testCapOrder = capOrderList.get(capOrderList.size() - 1);
        assertThat(testCapOrder.getRecipient()).isEqualTo(UPDATED_RECIPIENT);
        assertThat(testCapOrder.getTypeId()).isEqualTo(UPDATED_TYPE_ID);
        assertThat(testCapOrder.getTypeName()).isEqualTo(UPDATED_TYPE_NAME);
        assertThat(testCapOrder.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCapOrder.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testCapOrder.getDeliveryLocation()).isEqualTo(UPDATED_DELIVERY_LOCATION);
        assertThat(testCapOrder.getDeliveryPrice()).isEqualTo(UPDATED_DELIVERY_PRICE);
        assertThat(testCapOrder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    public void updateNonExistingCapOrder() throws Exception {
        int databaseSizeBeforeUpdate = capOrderRepository.findAll().size();

        // Create the CapOrder

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCapOrderMockMvc.perform(put("/api/cap-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capOrder)))
            .andExpect(status().isCreated());

        // Validate the CapOrder in the database
        List<CapOrder> capOrderList = capOrderRepository.findAll();
        assertThat(capOrderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteCapOrder() throws Exception {
        // Initialize the database
        capOrderRepository.save(capOrder);
        int databaseSizeBeforeDelete = capOrderRepository.findAll().size();

        // Get the capOrder
        restCapOrderMockMvc.perform(delete("/api/cap-orders/{id}", capOrder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CapOrder> capOrderList = capOrderRepository.findAll();
        assertThat(capOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CapOrder.class);
        CapOrder capOrder1 = new CapOrder();
        capOrder1.setId("id1");
        CapOrder capOrder2 = new CapOrder();
        capOrder2.setId(capOrder1.getId());
        assertThat(capOrder1).isEqualTo(capOrder2);
        capOrder2.setId("id2");
        assertThat(capOrder1).isNotEqualTo(capOrder2);
        capOrder1.setId(null);
        assertThat(capOrder1).isNotEqualTo(capOrder2);
    }
}
