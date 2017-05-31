package testutil

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.gradle.ReleaseConfiguration
import org.shipkit.internal.gradle.ReleaseConfigurationPlugin
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

    void setup(){
        initProject()
        createConfigFile()
        configureReleaseConfigurationDefaults()
    }

    void initProject() {
        project = new ProjectBuilder().withProjectDir(tmp.root).build()
    }

    void createConfigFile(){
        def rootPath = tmp.root.absolutePath
        def configFile = new File(rootPath + "/" + ReleaseConfigurationPlugin.CONFIG_FILE_RELATIVE_PATH);
        IOUtil.createParentDirectory(configFile)
        configFile << "shipkit { }"
    }

    ReleaseConfiguration applyReleaseConfiguration(){
        return project.plugins.apply(ReleaseConfigurationPlugin).configuration
    }

    void configureReleaseConfigurationDefaults(){
        def conf = applyReleaseConfiguration()
        conf.gitHub.readOnlyAuthToken = "token"
        conf.gitHub.repository = "repo"
    }
}
