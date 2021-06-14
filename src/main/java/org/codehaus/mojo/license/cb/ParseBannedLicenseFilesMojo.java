package org.codehaus.mojo.license.cb;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.license.AbstractLicenseMojo;
import org.codehaus.mojo.license.cb.model.Dependency;
import org.codehaus.mojo.license.cb.model.License;
import org.codehaus.mojo.license.cb.model.LicenseSummary;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Mojo( name = "parse-banned-license-files", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = false, requiresDirectInvocation = true )
public class ParseBannedLicenseFilesMojo extends AbstractLicenseMojo {

    private static final String GNU_GENERAL_PUBLIC_LICENSE_VERSION = "_gnu_general_public_license__version";

    private static final String GNU = "GNU";

    private static final String LICENSE_DEFINITION_PATH = "%s/src/license/licenses.xml";

    private static final String LICENSE_FILES_PATH = "%s/src/license/files";

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

        Set<String> gnu = new HashSet<>();
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        LicenseSummary summary = xmlMapper.readValue(new File(getLicensesDefinitionLocation(location)), LicenseSummary.class);
        for (Dependency dependency : summary.getDependencies().getDependency()) {
            for (License license : dependency.getLicenses().getLicense()) {
                if (license.getName() != null) {
                    if (Pattern.compile(Pattern.quote(GNU), Pattern.CASE_INSENSITIVE).matcher(license.getName()).find()) {
                        gnu.add(String.format("%s.%s", dependency.getGroupId(), dependency.getArtifactId()));
                    }
                } else {
                    System.out.println(String.format("[namaste]: [WARNING] %s:%s contains a license without name", dependency.getGroupId(), dependency.getArtifactId()));
                }
            }
        }

        if (gnu.isEmpty()) {
            System.out.println("[namaste]: [SUCCESS] Banned licenses check");
            return;
        }

        Set<String> banned = new HashSet<>();
        File folder = new File(getLicensesFilesLocation(location));
        for (String artifact : gnu) {
            File [] matches = exists(folder, String.format("%s*", artifact));
            if (matches == null) {
                continue;
            }

            if (matches.length == 1 && Pattern.compile(Pattern.quote(GNU_GENERAL_PUBLIC_LICENSE_VERSION), Pattern.CASE_INSENSITIVE).matcher(matches[0].getAbsolutePath()).find()) {
                banned.add(matches[0].getAbsolutePath());
            }
        }

        if (banned.isEmpty()) {
            System.out.println("[namaste]: [SUCCESS] Banned licenses check");
        } else {
            System.out.println("[namaste]: [ERROR] Banned licenses found!");
            for (String bannedLicense : banned) {
                System.out.println(String.format("[namaste]: - %s", bannedLicense));
            }
            throw new IllegalStateException("Banned licenses!");
        }
    }

    private String getLicensesFilesLocation(String path) {
        return String.format(LICENSE_FILES_PATH, path);
    }

    private String getLicensesDefinitionLocation(String path) {
        return String.format(LICENSE_DEFINITION_PATH, path);
    }

    /**
     * Checks if there exists an error in provided arguments
     * @param args
     */
    private void checkArguments() throws IllegalArgumentException {
        if (!Paths.get(location).toFile().exists()) {
            throw new IllegalArgumentException(String.format("Provided location %s does not exist", location));
        }

        String licenseDefinitionLocation = getLicensesDefinitionLocation(location);
        if (!Paths.get(licenseDefinitionLocation).toFile().exists()) {
            throw new IllegalArgumentException(String.format("Provided location %s does not exist", licenseDefinitionLocation));
        }

        String licenseFilesLocation = getLicensesFilesLocation(location);
        if (!Paths.get(licenseFilesLocation).toFile().exists()) {
            throw new IllegalArgumentException(String.format("Provided location %s does not exist", licenseFilesLocation));
        }

    }

    /**
     * Checks if a file exists in a folder given a wildcard
     * @param folder
     * @param wildcard
     * @return file exists in a folder given a wildcard
     */
    private File[] exists(File folder, String wildcard) {
        FileFilter filter = new WildcardFileFilter(wildcard);
        return folder.listFiles(filter);
    }

}
