package testutil

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.gradle.configuration.ShipkitConfiguration
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin
import org.shipkit.internal.notes.util.IOUtil
import spock.lang.Specification

/**
 * Base class for unit testing of Gradle plugins.
 * It initializes simple project with project directory set
 * and also creates ShipkitConfigurationPlugin.CONFIG_FILE_RELATIVE_PATH file
 * that is required by {@link org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin}
 * Configuration methods are overridable, so you don't have to rely on this behaviour if you don't need it.
 */
class PluginSpecification extends Specification {

    String projectVersion = "1.5.23"

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    Project project
    ShipkitConfiguration conf

    void setup() {
        initProject()
        createShipkitFile()
        createShipkitConfiguration()
    }

    void initProject() {
        project = new ProjectBuilder().withProjectDir(tmp.root).build()
        project.version = projectVersion
    }

    void createShipkitFile() {
        def rootPath = tmp.root.absolutePath
        def shipkitFile = new File(rootPath + "/gradle/shipkit.gradle")
        IOUtil.createParentDirectory(shipkitFile)
        shipkitFile << "shipkit { }"
    }

    ShipkitConfiguration applyShipkitConfiguration() {
        return project.plugins.apply(ShipkitConfigurationPlugin).configuration
    }

    void createShipkitConfiguration() {
        conf = applyShipkitConfiguration()
        this.conf.gitHub.readOnlyAuthToken = "token"
        this.conf.gitHub.repository = "repo"
        this.conf.releaseNotes.publicationRepository = "publicRepo"
    }
}
