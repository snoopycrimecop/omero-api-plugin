package org.openmicroscopy

class AbstractGroovyTest extends AbstractTest {

    public static final String PROJECT_NAME = 'omero-api-plugin'

    def setup() {
        setupBuildfile()
    }

    protected void setupBuildfile() {
        buildFile << """
            plugins {
                id 'org.openmicroscopy.api'
            }

            repositories {
                jcenter()
            }
        """
    }

    static String groovySettingsFile() {
        """
            rootProject.name = '$PROJECT_NAME'
        """
    }

    @Override
    String getBuildFileName() {
        'build.gradle'
    }

    @Override
    String getSettingsFileName() {
        'settings.gradle'
    }

}
