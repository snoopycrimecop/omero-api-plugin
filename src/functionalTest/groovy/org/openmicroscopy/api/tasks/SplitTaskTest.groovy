package org.openmicroscopy.api.tasks

import org.apache.commons.io.FilenameUtils
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.openmicroscopy.api.AbstractBaseTest

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
        File[] output = outputDir.listFiles()
        Collection<File> resultantFiles = output.findAll {
            FilenameUtils.getExtension(it.name) == "java"
        }
        output.length == resultantFiles.size()
    }




}
