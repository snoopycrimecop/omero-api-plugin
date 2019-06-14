package org.openmicroscopy.tasks

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.openmicroscopy.AbstractBaseTest

class SplitTaskTest extends AbstractBaseTest {

    def "can transform resulting file extensions to language extension"() {
        given:
        File outputDir = new File(projectDir, "splitResults")
        buildFile << """
            import org.openmicroscopy.api.tasks.SplitTask

            task simpleSplit(type: SplitTask) {
                language = "java"
                outputDir = file("$outputDir")
                source = file("$combinedDir")
            }
        """

        when:
        BuildResult result = build('simpleSplit')

        then:
        result.task(':simpleSplit').outcome == TaskOutcome.SUCCESS
        result.output.contains(".java")
        outputDir.listFiles().length > 0
    }


}
