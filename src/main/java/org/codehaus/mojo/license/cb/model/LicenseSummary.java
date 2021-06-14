package org.codehaus.mojo.license.cb.model;

/**
 *
 * @author imontero
 *
 */
public class LicenseSummary {

    private Dependencies dependencies;

    public LicenseSummary() {}

    public LicenseSummary(Dependencies dependencies) {
        this.dependencies = dependencies;
    }

    public void setDependencies(Dependencies dependencies) {
        this.dependencies = dependencies;
    }

    public Dependencies getDependencies() {
        return dependencies;
    }
}
