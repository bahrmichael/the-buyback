package com.thebuyback.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.thebuyback.eve.domain.enumeration.CapOrderStatus;

/**
 * A CapOrder.
 */
@Document(collection = "cap_order")
public class CapOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("recipient")
    private String recipient;

    @Field("type_id")
    private Integer typeId;

    @Field("type_name")
    private String typeName;

    @Field("price")
    private Double price;

    @Field("created")
    private Instant created;

    @Field("delivery_location")
    private String deliveryLocation;

    @Field("delivery_price")
    private Double deliveryPrice;

    @Field("status")
    private CapOrderStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public CapOrder recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public CapOrder typeId(Integer typeId) {
        this.typeId = typeId;
        return this;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public CapOrder typeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Double getPrice() {
        return price;
    }

    public CapOrder price(Double price) {
        this.price = price;
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Instant getCreated() {
        return created;
    }

    public CapOrder created(Instant created) {
        this.created = created;
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public CapOrder deliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
        return this;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public CapOrder deliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
        return this;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public CapOrderStatus getStatus() {
        return status;
    }

    public CapOrder status(CapOrderStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(CapOrderStatus status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CapOrder capOrder = (CapOrder) o;
        if (capOrder.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), capOrder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CapOrder{" +
            "id=" + getId() +
            ", recipient='" + getRecipient() + "'" +
            ", typeId=" + getTypeId() +
            ", typeName='" + getTypeName() + "'" +
            ", price=" + getPrice() +
            ", created='" + getCreated() + "'" +
            ", deliveryLocation='" + getDeliveryLocation() + "'" +
            ", deliveryPrice=" + getDeliveryPrice() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
