package testutil

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginSpecification extends Specification{
    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def project

    void setup(){
        initProject()
        createConfigFile()
    }

    public void initProject() {
        project = new ProjectBuilder().withProjectDir(tmp.root).build()
    }

    void createConfigFile(){
        def rootPath = tmp.root.absolutePath
        new File(rootPath + "/gradle").mkdir()
        new File(rootPath + "/gradle/shipkit.gradle") << "releasing { }"
    }
}
