package org.openmicroscopy.api.tasks

import org.apache.commons.io.FilenameUtils
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.openmicroscopy.api.AbstractBaseTest

class SplitTaskTest extends AbstractBaseTest {

    def "should create file with correct extension based on chosen language"() {
        given:
        File outputDir = new File(projectDir, "splitResults")
        buildFile << """
            import org.openmicroscopy.api.tasks.SplitTask

            task simpleSplit(type: SplitTask) {
                language = "python"
                outputDir = file("$outputDir")
                source = file("$combinedDir")
            }
        """

        when:
        BuildResult result = build('simpleSplit')

        then:
        result.task(':simpleSplit').outcome == TaskOutcome.SUCCESS
        File[] output = outputDir.listFiles()
        Collection<File> resultantFiles = output.findAll {
            FilenameUtils.getExtension(it.name) == "py"
        }
        output.length == resultantFiles.size()
    }

    def "should output java files in directory according to package"() {
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

        // Files should be located under package (org.openmicroscopy.example)
        File[] output = new File(outputDir, "org/openmicroscopy/example").listFiles()
        output.length == output.size()
    }


}
