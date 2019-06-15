package org.openmicroscopy.api

import org.gradle.testkit.runner.BuildResult

class ApiTest extends AbstractBaseTest {

    def "can pass with minimum configuration"() {
        given:
        File conventionOutputDir = new File(projectDir, "build/generated/sources/api")
        buildFile << """
            api {
                combinedFiles.from(files("$combinedDir"))
            
                language {
                    java {
                        outputDir "java"
                    }
                }
            }
        """

        when:
        BuildResult result = build("combinedToJava")

        then:
        conventionOutputDir.listFiles() > 0
    }


}
