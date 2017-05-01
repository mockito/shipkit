package org.mockito.release.internal.comparison

class PomFilter {

    String projectGroup;
    String previousVersion;
    String currentVersion;

    PomFilter(){

    }

    PomFilter(String projectGroup, String previousVersion, String currentVersion) {
        this.projectGroup = projectGroup
        this.previousVersion = previousVersion
        this.currentVersion = currentVersion
    }

    /**
     * modifies given pom:
     * - removes project version tag
     * - sets version to {@link #currentVersion} for dependencies with groupId equal to {@link #projectGroup}
     *      and version equal to {@link #previousVersion}
     * @return filtered pom
     */
    String filter(String pom){
        def projectXml = new XmlParser().parseText(pom)

        projectXml.remove(projectXml.version)

        if(!projectXml.dependencies.isEmpty()) {
            projectXml.dependencies[0].children().removeAll { dependency ->
                if(dependency.groupId.text() == projectGroup &&
                        (dependency.version.text() == previousVersion)){
                    dependency.version[0].setValue(currentVersion)
                }
            }
        }
        def stringWriter = new StringWriter()

        XmlNodePrinter nodePrinter = new XmlNodePrinter(new PrintWriter(stringWriter))
        nodePrinter.setPreserveWhitespace(true)
        nodePrinter.print(projectXml)

        return stringWriter.toString()
    }
}
