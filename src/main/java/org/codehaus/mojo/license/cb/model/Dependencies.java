package org.codehaus.mojo.license.cb.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author imontero
 *
 */
public class Dependencies {

    private List<Dependency> dependency = new ArrayList<>();

    public Dependencies() {}

    public Dependencies(List<Dependency> dependency) {
        this.dependency = dependency;
    }

    public void setDependency(List<Dependency> dependency) {
        this.dependency = dependency;
    }

    public List<Dependency> getDependency() {
        return dependency;
    }
}
