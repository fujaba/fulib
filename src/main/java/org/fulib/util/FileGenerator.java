package org.fulib.util;

import org.fulib.Generator;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public abstract class FileGenerator {

    private String customTemplatesFile;

    public abstract void generate(Clazz clazz);

    final void writeClassOrDeleteIfEmpty(FileFragmentMap fragmentMap, Clazz clazz, String filename, boolean overrideModFlag) {

        if ((clazz.getModified() || overrideModFlag) && fragmentMap.classBodyIsEmpty(fragmentMap)) {
            Path path = Paths.get(filename);
            try {
                Files.deleteIfExists(path);
                Logger.getLogger(Generator.class.getName())
                        .info("\n   deleting empty file " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fragmentMap.writeFile();
        }
    }

    final void writeClassOrDeleteIfEmpty(FileFragmentMap fragmentMap, Clazz clazz, String filename) {
        writeClassOrDeleteIfEmpty(fragmentMap, clazz, filename, false);
    }

    public final STGroup createSTGroup(String origFileName) {
        STGroup group;
        try {
            group = new STGroupFile(this.customTemplatesFile);
            STGroup origGroup = new STGroupFile(origFileName);
            group.importTemplates(origGroup);
        } catch (Exception e) {
            group = new STGroupFile(origFileName);
        }
        group.registerRenderer(String.class, new StringRenderer());
        return group;
    }

    public String getCustomTemplatesFile() {
        return customTemplatesFile;
    }

    public FileGenerator setCustomTemplatesFile(String customTemplateFile) {
        this.customTemplatesFile = customTemplateFile;
        return this;
    }
}
