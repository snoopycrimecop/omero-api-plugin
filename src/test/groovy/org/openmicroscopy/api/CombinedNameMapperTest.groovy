package org.openmicroscopy.api

import org.openmicroscopy.api.utils.CombinedNameMapper
import spock.lang.Specification

class CombinedNameMapperTest extends Specification {

    def "correctly detects combined file extension and results in input filename by default"() {
        given:
        String combined = "somefileI.combined"

        when:
        CombinedNameMapper mapper = new CombinedNameMapper()
        String result = mapper.transform(combined)

        then:
        result == "somefileI"
    }

    def "fails if file extension is not combined"() {
        given:
        String combined = "somefileI.txt"

        when:
        CombinedNameMapper mapper = new CombinedNameMapper()
        String result = mapper.transform(combined)

        then:
        result == null
    }

    def "correctly replaces combined file name"() {
        given:
        String combined = "somefileI.combined"
        String replacement = "somefileI-hello"

        when:
        CombinedNameMapper mapper = new CombinedNameMapper("\$1-hello")
        String result = mapper.transform(combined)

        then:
        result == replacement
    }

}
