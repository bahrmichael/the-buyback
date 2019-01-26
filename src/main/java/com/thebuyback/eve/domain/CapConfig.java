package com.thebuyback.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Objects;

/**
 * A CapConfig.
 */
@Document(collection = "cap_config")
public class CapConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("type_id")
    private Integer typeId;

    @Field("type_name")
    private String typeName;

    @Field("price")
    private Double price;

    @Field("delivery_location_1")
    private String deliveryLocation1;

    @Field("delivery_price_1")
    private Double deliveryPrice1;

    @Field("delivery_location_2")
    private String deliveryLocation2;

    @Field("delivery_price_2")
    private Double deliveryPrice2;

    @Field("delivery_location_3")
    private String deliveryLocation3;

    @Field("delivery_price_3")
    private Double deliveryPrice3;

    @Field("delivery_location_4")
    private String deliveryLocation4;

    @Field("delivery_price_4")
    private Double deliveryPrice4;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public CapConfig typeId(Integer typeId) {
        this.typeId = typeId;
        return this;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public CapConfig typeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Double getPrice() {
        return price;
    }

    public CapConfig price(Double price) {
        this.price = price;
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDeliveryLocation1() {
        return deliveryLocation1;
    }

    public CapConfig deliveryLocation1(String deliveryLocation1) {
        this.deliveryLocation1 = deliveryLocation1;
        return this;
    }

    public void setDeliveryLocation1(String deliveryLocation1) {
        this.deliveryLocation1 = deliveryLocation1;
    }

    public Double getDeliveryPrice1() {
        return deliveryPrice1;
    }

    public CapConfig deliveryPrice1(Double deliveryPrice1) {
        this.deliveryPrice1 = deliveryPrice1;
        return this;
    }

    public void setDeliveryPrice1(Double deliveryPrice1) {
        this.deliveryPrice1 = deliveryPrice1;
    }

    public String getDeliveryLocation2() {
        return deliveryLocation2;
    }

    public CapConfig deliveryLocation2(String deliveryLocation2) {
        this.deliveryLocation2 = deliveryLocation2;
        return this;
    }

    public void setDeliveryLocation2(String deliveryLocation2) {
        this.deliveryLocation2 = deliveryLocation2;
    }

    public Double getDeliveryPrice2() {
        return deliveryPrice2;
    }

    public CapConfig deliveryPrice2(Double deliveryPrice2) {
        this.deliveryPrice2 = deliveryPrice2;
        return this;
    }

    public void setDeliveryPrice2(Double deliveryPrice2) {
        this.deliveryPrice2 = deliveryPrice2;
    }

    public String getDeliveryLocation3() {
        return deliveryLocation3;
    }

    public CapConfig deliveryLocation3(String deliveryLocation3) {
        this.deliveryLocation3 = deliveryLocation3;
        return this;
    }

    public void setDeliveryLocation3(String deliveryLocation3) {
        this.deliveryLocation3 = deliveryLocation3;
    }

    public Double getDeliveryPrice3() {
        return deliveryPrice3;
    }

    public CapConfig deliveryPrice3(Double deliveryPrice3) {
        this.deliveryPrice3 = deliveryPrice3;
        return this;
    }

    public void setDeliveryPrice3(Double deliveryPrice3) {
        this.deliveryPrice3 = deliveryPrice3;
    }

    public String getDeliveryLocation4() {
        return deliveryLocation4;
    }

    public CapConfig deliveryLocation4(String deliveryLocation4) {
        this.deliveryLocation4 = deliveryLocation4;
        return this;
    }

    public void setDeliveryLocation4(String deliveryLocation4) {
        this.deliveryLocation4 = deliveryLocation4;
    }

    public Double getDeliveryPrice4() {
        return deliveryPrice4;
    }

    public CapConfig deliveryPrice4(Double deliveryPrice4) {
        this.deliveryPrice4 = deliveryPrice4;
        return this;
    }

    public void setDeliveryPrice4(Double deliveryPrice4) {
        this.deliveryPrice4 = deliveryPrice4;
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
        CapConfig capConfig = (CapConfig) o;
        if (capConfig.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), capConfig.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CapConfig{" +
            "id=" + getId() +
            ", typeId=" + getTypeId() +
            ", typeName='" + getTypeName() + "'" +
            ", price=" + getPrice() +
            ", deliveryLocation1='" + getDeliveryLocation1() + "'" +
            ", deliveryPrice1=" + getDeliveryPrice1() +
            ", deliveryLocation2='" + getDeliveryLocation2() + "'" +
            ", deliveryPrice2=" + getDeliveryPrice2() +
            ", deliveryLocation3='" + getDeliveryLocation3() + "'" +
            ", deliveryPrice3=" + getDeliveryPrice3() +
            ", deliveryLocation4='" + getDeliveryLocation4() + "'" +
            ", deliveryPrice4=" + getDeliveryPrice4() +
            "}";
    }
}
