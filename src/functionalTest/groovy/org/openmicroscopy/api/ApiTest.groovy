package org.openmicroscopy.api

import groovy.io.FileType

class ApiTest extends AbstractBaseTest {

    def "can pass with minimum configuration"() {
        given:
        List<File> results = []
        File conventionOutputDir = new File(projectDir, "build/generated/sources/api")
        buildFile << """
            api {
                combinedFiles.from(files("$combinedDir"))
            
                language {
                    java {}
                }
            }
        """

        when:
        build("combinedToJava")

        then:
        conventionOutputDir.traverse(type: FileType.FILES) { it -> results.add it }
        !results.isEmpty()
    }

}
