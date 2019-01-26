package com.thebuyback.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.thebuyback.eve.domain.enumeration.ManufacturingOrderStatus;

/**
 * A ManufacturingOrder.
 */
@Document(collection = "manufacturing_order")
public class ManufacturingOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("price_per_unit")
    private Double pricePerUnit;

    @Field("type_name")
    private String typeName;

    @Field("amount")
    private Integer amount;

    @Field("instant")
    private Instant instant;

    @Field("assignee")
    private String assignee;

    @Field("status")
    private ManufacturingOrderStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public ManufacturingOrder pricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getTypeName() {
        return typeName;
    }

    public ManufacturingOrder typeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getAmount() {
        return amount;
    }

    public ManufacturingOrder amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Instant getInstant() {
        return instant;
    }

    public ManufacturingOrder instant(Instant instant) {
        this.instant = instant;
        return this;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public String getAssignee() {
        return assignee;
    }

    public ManufacturingOrder assignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public ManufacturingOrderStatus getStatus() {
        return status;
    }

    public ManufacturingOrder status(ManufacturingOrderStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ManufacturingOrderStatus status) {
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
        ManufacturingOrder manufacturingOrder = (ManufacturingOrder) o;
        if (manufacturingOrder.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), manufacturingOrder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ManufacturingOrder{" +
            "id=" + getId() +
            ", pricePerUnit=" + getPricePerUnit() +
            ", typeName='" + getTypeName() + "'" +
            ", amount=" + getAmount() +
            ", instant='" + getInstant() + "'" +
            ", assignee='" + getAssignee() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
