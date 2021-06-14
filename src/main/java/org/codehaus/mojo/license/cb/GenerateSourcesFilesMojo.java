package org.codehaus.mojo.license.cb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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

@Mojo( name = "generate-sources-files", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = false, requiresDirectInvocation = true )
public class GenerateSourcesFilesMojo extends AbstractLicenseMojo {

    private static final String GNU = "GNU";

    private static final String GPL = "GPL";

    private static final String CDDL = "CDDL";

    private static final String LICENSE_DEFINITION_PATH = "%s/src/license/licenses.xml";

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
        String sourcesList = String.format("%s/sourcesList.txt", location);
        String sourcesToDownload = String.format("%s/sourcesToDownload.properties", location);
        OutputStreamWriter sourcesListFile = new OutputStreamWriter(new FileOutputStream(new File(sourcesList)), StandardCharsets.UTF_8);
        OutputStreamWriter sourcesToDownloadFile = new OutputStreamWriter(new FileOutputStream(new File(sourcesToDownload)), StandardCharsets.UTF_8);
        sourcesListFile.write(String.format("GPL, LGPL and CDDL Artifacts%n"));

        Set<String> artifactIds = new HashSet<>();
        Set<String> groupIds = new HashSet<>();
        Set<String> artifacts = new HashSet<>();
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        LicenseSummary summary = xmlMapper.readValue(new File(getLicensesDefinitionLocation(location)), LicenseSummary.class);
        for (Dependency dependency : summary.getDependencies().getDependency()) {
            for (License license : dependency.getLicenses().getLicense()) {
                if (license.getName() != null) {
                    if (Pattern.compile(Pattern.quote(GNU), Pattern.CASE_INSENSITIVE).matcher(license.getName()).find() ||
                        Pattern.compile(Pattern.quote(GPL), Pattern.CASE_INSENSITIVE).matcher(license.getName()).find() ||
                        Pattern.compile(Pattern.quote(CDDL), Pattern.CASE_INSENSITIVE).matcher(license.getName()).find()) {
                        artifacts.add(String.format("%s:%s", dependency.getGroupId(), dependency.getArtifactId()));
                        artifactIds.add(dependency.getArtifactId());
                        groupIds.add(dependency.getGroupId());
                    }
                } else {
                    System.out.println(String.format("[namaste]: [WARNING] %s:%s contains a license without name", dependency.getGroupId(), dependency.getArtifactId()));
                }
            }
        }

        for(String source: artifacts) {
            sourcesListFile.write(String.format("%s%n",source));
        }
        sourcesListFile.close();
        sourcesToDownloadFile.write(String.format("artifactsIds=%s%n", String.join(",", artifactIds)));
        sourcesToDownloadFile.write(String.format("groupIds=%s%n", String.join(",", groupIds)));
        sourcesToDownloadFile.close();

        showContent(sourcesList);
        showContent(sourcesToDownload);
    }

    private void showContent(String sources) throws IOException {
        System.out.println(String.format("[namaste]: [SUCCESS] Generated %s file with the following content:%n", sources));
        for (String line : Files.readAllLines(Paths.get(sources))) {
            System.out.println(line);
        }
        System.out.println();
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

        String licensesDefinitionLocation = getLicensesDefinitionLocation(location);
        if (!Paths.get(licensesDefinitionLocation).toFile().exists()) {
            throw new IllegalArgumentException(String.format("Provided location %s does not exist", licensesDefinitionLocation));
        }
    }
}
