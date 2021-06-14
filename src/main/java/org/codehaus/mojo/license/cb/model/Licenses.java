package org.codehaus.mojo.license.cb.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author imontero
 *
 */
public class Licenses {

    private List<License> license = new ArrayList<>();

    public Licenses() {}

    public Licenses(List<License> license) {
        this.license = license;
    }

    public List<License> getLicense() {
        return license;
    }

    public void setLicense(List<License> license) {
        this.license = license;
    }

}
