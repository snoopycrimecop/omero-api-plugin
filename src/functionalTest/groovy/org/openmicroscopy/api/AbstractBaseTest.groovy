package org.openmicroscopy.api

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class AbstractBaseTest extends AbstractGroovyTest {

    File combinedDir

    def setup() {
        combinedDir = new File(projectDir, "src/main/resources/combined")
        copyCombinedFiles(combinedDir)
        writeSettingsFile()
    }

    private void writeSettingsFile() {
        settingsFile << groovySettingsFile()
    }

    private void copyCombinedFiles(File outputDir) {
        Path simple = getResource("/simple.combined")
        copyFile(simple, outputDir.toPath())
    }

    private void copyFile(Path fileToCopy, Path targetDir) {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir)
        }

        Path targetFile = targetDir.resolve(fileToCopy.getFileName())
        Files.copy(fileToCopy, targetFile, StandardCopyOption.REPLACE_EXISTING)
    }

    private Path getResource(String name) {
        Paths.get(Paths.getResource(name).toURI())
    }

}
