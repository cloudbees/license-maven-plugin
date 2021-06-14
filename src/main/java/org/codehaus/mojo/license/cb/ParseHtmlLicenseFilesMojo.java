package org.codehaus.mojo.license.cb;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.license.AbstractLicenseMojo;
import org.unix4j.Unix4j;

@Mojo( name = "parse-html-license-files", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = false, requiresDirectInvocation = true )
public class ParseHtmlLicenseFilesMojo extends AbstractLicenseMojo {

    private static final String LICENSE_FILES_PATH = "%s/src/license/files/";

    @Parameter( property = "location" )
    private String location;

    @Override
    public boolean isSkip() {
        // can't skip this goal since direct invocation is required
        return false;
    }

    @Override
    protected void init() throws Exception {
        // do nothing
    }

    @Override
    protected void doAction() throws Exception {
        checkArguments();
        File folder = new File(getLicenseFilesLocation(location));
        File[] files = folder.listFiles();
        Set<String> html = new HashSet<>();
        if (files != null) {
            for (File license : files) {
                if (!Unix4j.grep("</html>", license).toLineList().isEmpty()) {
                    html.add(license.getName());
                }
            }
            if (!html.isEmpty()) {
                System.out.println("[namaste]: [ERROR] HTML licenses!");
                for (String htmlLicense: html) {
                    System.out.println(String.format("[namaste]: - %s", htmlLicense));
                }
                throw new IllegalStateException("HTML licenses!");
            }
        }
        System.out.println("[namaste]: [SUCCESS] HTML licenses check");

    }

    private static String getLicenseFilesLocation(String path) {
        return String.format(LICENSE_FILES_PATH, path);
    }

    /**
     * Checks if there exists an error in provided arguments
     * @param args
     */
    private void checkArguments() throws IllegalArgumentException {
        if (!Paths.get(location).toFile().exists()) {
            throw new IllegalArgumentException(String.format("Provided location %s does not exist", location));
        }

        String licenseFilesLocation = getLicenseFilesLocation(location);
        if (!Paths.get(licenseFilesLocation).toFile().exists()) {
            throw new IllegalArgumentException(String.format("Provided location %s does not exist", licenseFilesLocation));
        }
    }
}
