package org.mockito.release.internal.comparison

class PomSiblingDependencyRemover {

    String removeSiblingDependencies(String pom, Set<BaseProjectProperties> siblingProjects){
        def projectXml = new XmlParser().parseText(pom)

        projectXml.remove(projectXml.version)

        if(!projectXml.dependencies.isEmpty()) {
            projectXml.dependencies[0].children().removeAll { dependency ->
                siblingProjects.contains(
                        new BaseProjectProperties(dependency.groupId.text(), dependency.artifactId.text())
                );
            }
        }
        def stringWriter = new StringWriter()

        XmlNodePrinter nodePrinter = new XmlNodePrinter(new PrintWriter(stringWriter))
        nodePrinter.setPreserveWhitespace(true)
        nodePrinter.print(projectXml)

        return stringWriter.toString()
    }
}
