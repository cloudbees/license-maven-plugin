package org.codehaus.mojo.license.cb.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 *
 * @author imontero
 *
 */
public class License {

    @JacksonXmlProperty(isAttribute = true)
    private String approved = "true";

    @JacksonXmlProperty(isAttribute = true)
    private String accepted = "true";

    private String name;

    private String url;

    private String file;

    private String distribution;

    private String comments;

    public License() {}

    public License(String name, String url, String file, String distribution, String comments) {
        this.name = name;
        this.url = url;
        this.file = file;
        this.distribution = distribution;
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public String getDistribution() {
        return distribution;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getFile() {
        return file;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApproved() {
        return approved;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }
}
