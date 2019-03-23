package com.thebuyback.eve.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "type_ingredients")
public class TypeIngredients {

    @Id
    private String id;
    private long typeId;
    private int quantityToReprocess;
    private List<ItemWithQuantity> ingredients;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public int getQuantityToReprocess() {
        return quantityToReprocess;
    }

    public void setQuantityToReprocess(final int quantityToReprocess) {
        this.quantityToReprocess = quantityToReprocess;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(final long typeId) {
        this.typeId = typeId;
    }

    public List<ItemWithQuantity> getIngredients() {
        return ingredients;
    }

    public void setIngredients(final List<ItemWithQuantity> ingredients) {
        this.ingredients = ingredients;
    }
}
