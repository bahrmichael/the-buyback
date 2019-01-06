package com.thebuyback.eve.domain;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;

/**
 * Contract
 *
 * Created on 08.11.2017
 *
 *
 */
public class Contract {
    @Id
    private long id;
    private long issuerId;
    private long issuerCorporationId;
    private long assigneeId;
    private String status;
    private long startLocationId;
    private double price;
    private double reward;
    private Map<Integer, Integer> items;
    private String appraisalLink;
    private double buyValue;
    private double sellValue;
    private String title;
    private Instant dateIssued;
    private Instant dateCompleted;
    private String client;

    private boolean declineMailSent;
    private boolean approved;
    private Double oreValue;
    private Double otherValue;
    private Double buybackPrice;

    public Contract() {
    }

    public Contract(final long id, final long issuerId, final long issuerCorporationId,
                    final long assigneeId, final String status, final long startLocationId,
                    final double price, final double reward, final Map<Integer, Integer> items,
                    final String appraisalLink,
                    final double buyValue, final double sellValue, final String title,
                    final Instant dateIssued, final Instant dateCompleted, final String client,
                    final boolean declineMailSent, final boolean approved) {
        this.id = id;
        this.issuerId = issuerId;
        this.issuerCorporationId = issuerCorporationId;
        this.assigneeId = assigneeId;
        this.status = status;
        this.startLocationId = startLocationId;
        this.price = price;
        this.reward = reward;
        this.items = items;
        this.appraisalLink = appraisalLink;
        this.buyValue = buyValue;
        this.sellValue = sellValue;
        this.title = title;
        this.dateIssued = dateIssued;
        this.dateCompleted = dateCompleted;
        this.client = client;
        this.declineMailSent = declineMailSent;
        this.approved = approved;
    }

    public Double getBuybackPrice() {
        return buybackPrice;
    }

    public void setBuybackPrice(final double buybackPrice) {
        this.buybackPrice = buybackPrice;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public boolean isDeclineMailSent() {
        return declineMailSent;
    }

    public void setDeclineMailSent(final boolean declineMailSent) {
        this.declineMailSent = declineMailSent;
    }

    public Instant getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(final Instant dateIssued) {
        this.dateIssued = dateIssued;
    }

    public Instant getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(final Instant dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setIssuerId(final long issuerId) {
        this.issuerId = issuerId;
    }

    public void setIssuerCorporationId(final long issuerCorporationId) {
        this.issuerCorporationId = issuerCorporationId;
    }

    public void setAssigneeId(final long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setStartLocationId(final long startLocationId) {
        this.startLocationId = startLocationId;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public String getAppraisalLink() {
        return appraisalLink;
    }

    public void setAppraisalLink(final String appraisalLink) {
        this.appraisalLink = appraisalLink;
    }

    public double getBuyValue() {
        return buyValue;
    }

    public void setBuyValue(final double buyValue) {
        this.buyValue = buyValue;
    }

    public double getSellValue() {
        return sellValue;
    }

    public void setSellValue(final double sellValue) {
        this.sellValue = sellValue;
    }

    public long getIssuerId() {
        return issuerId;
    }

    public long getIssuerCorporationId() {
        return issuerCorporationId;
    }

    public long getAssigneeId() {
        return assigneeId;
    }

    public String getStatus() {
        return status;
    }

    public long getStartLocationId() {
        return startLocationId;
    }

    public double getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    public void setItems(final Map<Integer, Integer> items) {
        this.items = items;
    }

    public String getClient() {
        return client;
    }

    public void setClient(final String client) {
        this.client = client;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(final double reward) {
        this.reward = reward;
    }

    public Double getOreValue() {
        return oreValue;
    }

    public void setOreValue(final Double oreValue) {
        this.oreValue = oreValue;
    }

    public Double getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(final Double otherValue) {
        this.otherValue = otherValue;
    }
}
