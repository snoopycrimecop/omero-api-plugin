package org.openmicroscopy.api

import org.apache.commons.io.FilenameUtils
import org.gradle.api.Transformer
import org.openmicroscopy.api.types.Prefix
import org.openmicroscopy.api.utils.ExtensionTransformer
import spock.lang.Specification

class ExtensionTransformerTest extends Specification {

    def "can handle inputs with combined file extension"() {
        given:
        String filename = "example.combined"
        Prefix prefix = Prefix.JAV

        when:
        String result = new ExtensionTransformer(prefix, new SimpleTransformer())
                .transform(filename)

        then:
        FilenameUtils.getExtension(result) == prefix.extension
    }

    def "can handle inputs with no file extension"() {
        given:
        String filename = "example"
        Prefix prefix = Prefix.JAV

        when:
        String result = new ExtensionTransformer(prefix, new SimpleTransformer())
                .transform(filename)

        then:
        FilenameUtils.getExtension(result) == prefix.extension
    }

    /**
     * Does nothing but return the input string as the output
     */
    class SimpleTransformer implements Transformer<String, String> {
        @Override
        String transform(String s) {
            return s
        }
    }


}
