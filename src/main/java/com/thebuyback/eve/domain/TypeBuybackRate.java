package com.thebuyback.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Objects;

import com.thebuyback.eve.domain.enumeration.TypeCategory;

/**
 * A TypeBuybackRate.
 */
@Document(collection = "type_buyback_rate")
public class TypeBuybackRate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("type_id")
    private Long typeId;

    @Field("type_name")
    private String typeName;

    @Field("category")
    private TypeCategory category;

    @Field("rate")
    private Double rate;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public TypeBuybackRate typeId(Long typeId) {
        this.typeId = typeId;
        return this;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public TypeBuybackRate typeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public TypeCategory getCategory() {
        return category;
    }

    public TypeBuybackRate category(TypeCategory category) {
        this.category = category;
        return this;
    }

    public void setCategory(TypeCategory category) {
        this.category = category;
    }

    public Double getRate() {
        return rate;
    }

    public TypeBuybackRate rate(Double rate) {
        this.rate = rate;
        return this;
    }

    public void setRate(Double rate) {
        this.rate = rate;
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
        TypeBuybackRate typeBuybackRate = (TypeBuybackRate) o;
        if (typeBuybackRate.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), typeBuybackRate.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TypeBuybackRate{" +
            "id=" + getId() +
            ", typeId=" + getTypeId() +
            ", typeName='" + getTypeName() + "'" +
            ", category='" + getCategory() + "'" +
            ", rate=" + getRate() +
            "}";
    }
}
