package com.thebuyback.eve.domain;

import com.mashape.unirest.request.body.Body;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppraisalFailed extends Exception {

    private static final long serialVersionUID = 2781884538931652756L;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String TEMPLATE = "Request to %s failed: Status '%s'";
    private final String url;
    private final Body body;
    private String statusText;

    public AppraisalFailed(final String url, final Body body) {
        this.url = url;
        this.body = body;
    }

    public AppraisalFailed(final String url, final Body body, final String statusText) {
        this.url = url;
        this.body = body;
        this.statusText = statusText;
    }

    @Override
    public String getMessage() {
        return String.format(TEMPLATE, url, statusText);
    }

    public String getStatusText() {
        return statusText;
    }
}
