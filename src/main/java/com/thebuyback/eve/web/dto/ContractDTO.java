package com.thebuyback.eve.web.dto;

import java.time.Instant;

/**
 * ContractDTO
 *
 * Created on 08.11.2017
 *
 */
public class ContractDTO {
    private long id;
    private String client;
    private Instant created;
    private double price;
    private double reward;
    private double jitaBuy;
    private double jitaSell;
    private boolean declineMailSent;
    private boolean approved;
    private Double buybackPrice;
    private String appraisalLink;
    private boolean acceptedLocation;

    public ContractDTO() {
    }

    public ContractDTO(final long id, final String client, final Instant created, final double price,
                       final double reward,
                       final double jitaBuy, final double jitaSell, final boolean declineMailSent,
                       final boolean approved,
                       final Double buybackPrice, final String appraisalLink, final boolean acceptedLocation) {
        this.id = id;
        this.client = client;
        this.created = created;
        this.price = price;
        this.reward = reward;
        this.jitaBuy = jitaBuy;
        this.jitaSell = jitaSell;
        this.declineMailSent = declineMailSent;
        this.approved = approved;
        this.buybackPrice = buybackPrice;
        this.appraisalLink = appraisalLink;
        this.acceptedLocation = acceptedLocation;
    }

    public boolean isAcceptedLocation() {
        return acceptedLocation;
    }

    public void setAcceptedLocation(final boolean acceptedLocation) {
        this.acceptedLocation = acceptedLocation;
    }

    public double getJitaSell() {
        return jitaSell;
    }

    public void setJitaSell(final double jitaSell) {
        this.jitaSell = jitaSell;
    }

    public String getAppraisalLink() {
        return appraisalLink;
    }

    public void setAppraisalLink(final String appraisalLink) {
        this.appraisalLink = appraisalLink;
    }

    public Double getBuybackPrice() {
        return buybackPrice;
    }

    public void setBuybackPrice(final Double buybackPrice) {
        this.buybackPrice = buybackPrice;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isDeclineMailSent() {
        return declineMailSent;
    }

    public void setDeclineMailSent(final boolean declineMailSent) {
        this.declineMailSent = declineMailSent;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(final String client) {
        this.client = client;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(final Instant created) {
        this.created = created;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public double getJitaBuy() {
        return jitaBuy;
    }

    public void setJitaBuy(final double jitaBuy) {
        this.jitaBuy = jitaBuy;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(final double reward) {
        this.reward = reward;
    }
}
