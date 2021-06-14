package org.codehaus.mojo.license.cb.model;

/**
 *
 * @author imontero
 *
 */
public class Dependency {

    private String groupId;

    private String artifactId;

    private String version;

    private Licenses licenses;

    public Dependency() {}

    public Dependency(String groupId, String artifactId, String version, Licenses licenses) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.licenses = licenses;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setLicenses(Licenses licenses) {
        this.licenses = licenses;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public Licenses getLicenses() {
        return licenses;
    }

}
