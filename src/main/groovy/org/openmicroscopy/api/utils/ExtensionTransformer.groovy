package org.openmicroscopy.api.utils

import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.gradle.api.Transformer
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.openmicroscopy.api.types.Prefix

@CompileStatic
class ExtensionTransformer implements Transformer<String, String> {

    final Prefix prefix
    final Transformer<String, String> transformer

    private static final Logger Log = Logging.getLogger(ExtensionTransformer)

    ExtensionTransformer(Prefix prefix, Transformer<String, String> transformer) {
        this.prefix = prefix
        this.transformer = transformer
    }

    @Override
    String transform(String s) {
        String name = transformer.transform(s)

        // Add a file extension if one isn't present or is default (.combined)
        String fileExtension = FilenameUtils.getExtension(name)
        if (fileExtension == null || fileExtension == "combined") {
            name = FilenameUtils.getBaseName(name) + prefix.extension
            Log.quiet("Transforming extension from .$fileExtension to .$prefix.extension")
        }

        return name
    }

}