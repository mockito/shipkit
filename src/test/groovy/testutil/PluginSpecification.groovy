package testutil

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.gradle.ReleaseConfiguration
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin
import org.shipkit.internal.notes.util.IOUtil
import spock.lang.Specification

/**
 * Base class for unit testing of Gradle plugins.
 * It initializes simple project with project directory set
 * and also creates ReleaseConfigurationPlugin.CONFIG_FILE_RELATIVE_PATH file
 * that is required by {@link ReleaseConfigurationPlugin}
 * Configuration methods are overridable, so you don't have to rely on this behaviour if you don't need it.
 */
class PluginSpecification extends Specification{
    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    Project project
    ReleaseConfiguration conf

    void setup(){
        initProject()
        createShipkitFile()
        createReleaseConfiguration()
    }

    void initProject() {
        project = new ProjectBuilder().withProjectDir(tmp.root).build()
    }

    void createShipkitFile(){
        def rootPath = tmp.root.absolutePath
        def shipkitFile = new File(rootPath + "/" + ReleaseConfigurationPlugin.SHIPKIT_FILE_RELATIVE_PATH);
        IOUtil.createParentDirectory(shipkitFile)
        shipkitFile << "shipkit { }"
    }

    ReleaseConfiguration applyReleaseConfiguration(){
        return project.plugins.apply(ReleaseConfigurationPlugin).configuration
    }

    void createReleaseConfiguration(){
        conf = applyReleaseConfiguration()
        this.conf.gitHub.readOnlyAuthToken = "token"
        this.conf.gitHub.repository = "repo"
    }
}
