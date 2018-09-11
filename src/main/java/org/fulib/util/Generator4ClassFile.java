package org.fulib.util;

import org.fulib.Generator;
import org.fulib.Parser;
import org.fulib.StrUtil;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Generator4ClassFile {
    public void doGenerate(Clazz clazz) {
        String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
        FileFragmentMap fragmentMap = Parser.parse(classFileName);

        // doGenerate code for class
        generatePackageDecl(clazz, fragmentMap);

        generateClassDecl(clazz, fragmentMap);

        generateAttributes(clazz, fragmentMap);

        generateAssociations(clazz, fragmentMap);

        generatePropertyChangeSupport(clazz, fragmentMap);

        generateToString(clazz, fragmentMap);

        fragmentMap.add(Parser.CLASS_END, "}", 1);

        if (clazz.getModified() == true && fragmentMap.classBodyIsEmpty(fragmentMap)) {
            Path path = Paths.get(classFileName);
            try {
                Files.deleteIfExists(path);
                Logger.getLogger(Generator.class.getName())
                        .info("\n   deleting empty file " + classFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fragmentMap.writeFile();
        }
    }

    private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap) {
        String result = String.format("package %s;", clazz.getModel().getPackageName());
        fragmentMap.add(Parser.PACKAGE, result, 2);
    }


    private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap) {
        String result = String.format("public class %s\n{", clazz.getName());
        fragmentMap.add(Parser.CLASS, result, 2);
    }


    private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap) {
        for (Attribute attr : clazz.getAttributes()) {
            generateAttributeDeclaration(fragmentMap, attr);

            generateGetMethod(fragmentMap, attr);

            generateSetMethod(fragmentMap, attr);
        }
    }


    private void generateAttributeDeclaration(FileFragmentMap fragmentMap, Attribute attr) {
        STGroup stg = new STGroupFile("templates/attributes.stg");
        ST attrTemplate = stg.getInstanceOf("attrDecl");
        attrTemplate.add("type", attr.getType());
        attrTemplate.add("name", attr.getName());
        attrTemplate.add("value", attr.getInitialization());
        String result = attrTemplate.render();

        fragmentMap.add(Parser.ATTRIBUTE + ":" + attr.getName(), result, 2, attr.getModified());
    }


    private void generateGetMethod(FileFragmentMap fragmentMap, Attribute attr) {
        STGroup group = new STGroupFile("templates/attributes.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST attrTemplate = group.getInstanceOf("attrGet");
        attrTemplate.add("type", attr.getType());
        attrTemplate.add("name", attr.getName());
        String result = attrTemplate.render();

        fragmentMap.add(Parser.METHOD + ":get" + StrUtil.cap(attr.getName()) + "()", result, 2, attr.getModified());
    }


    private void generateSetMethod(FileFragmentMap fragmentMap, Attribute attr) {
        STGroup group = new STGroupFile("templates/attributes.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST attrTemplate = group.getInstanceOf("attrSet");
        attrTemplate.add("class", attr.getClazz().getName());
        attrTemplate.add("type", attr.getType());
        attrTemplate.add("name", attr.getName());
        attrTemplate.add("useEquals", attr.getType().equals("String"));
        String result = attrTemplate.render();

        fragmentMap.add(Parser.METHOD + ":set" + StrUtil.cap(attr.getName()) + "(" + attr.getType() + ")", result, 3, attr.getModified());

    }


    private void generateAssociations(Clazz clazz, FileFragmentMap fragmentMap) {
        String result;
        ST st;
        STGroup group;
        for (AssocRole role : clazz.getRoles()) {
            group = new STGroupFile("templates/associations.stg");
            group.registerRenderer(String.class, new StringRenderer());
            String roleType = role.getOther().getClazz().getName();

            // provide empty_set in this class
            if (role.getCardinality() != ClassModelBuilder.ONE) {
                roleType = String.format(role.getRoleType(), role.getOther().getClazz().getName());

                st = group.getInstanceOf("emptySetDecl");
                st.add("roleName", role.getName());
                st.add("otherClassName", role.getOther().getClazz().getName());
                st.add("roleType", roleType);
                result = st.render();

                fragmentMap.add(Parser.ATTRIBUTE + ":EMPTY_" + role.getName(), result, 3, role.getModified());
            }


            st = group.getInstanceOf("roleAttrDecl");
            st.add("roleName", role.getName());
            st.add("roleType", roleType);
            result = st.render();

            fragmentMap.add(Parser.ATTRIBUTE + ":" + role.getName(), result, 2, role.getModified());


            st = group.getInstanceOf("getMethod");

            st.add("roleName", role.getName());
            st.add("toMany", role.getCardinality() != ClassModelBuilder.ONE);
            st.add("otherClassName", role.getOther().getClazz().getName());
            st.add("roleType", roleType);
            result = st.render();

            fragmentMap.add(Parser.METHOD + ":get" + StrUtil.cap(role.getName()) + "()", result, 2, role.getModified());


            st = group.getInstanceOf("setMethod");
            st.add("roleName", role.getName());
            st.add("toMany", role.getCardinality() != ClassModelBuilder.ONE);
            st.add("myClassName", clazz.getName());
            st.add("otherClassName", role.getOther().getClazz().getName());
            st.add("otherRoleName", role.getOther().getName());
            st.add("otherToMany", role.getOther().getCardinality() != ClassModelBuilder.ONE);
            st.add("roleType", roleType);
            result = st.render();

            String signature = "set";
            String paramType = role.getOther().getClazz().getName();
            if (role.getCardinality() != ClassModelBuilder.ONE) {
                signature = "with";
                paramType = "Object...";
            }

            signature += StrUtil.cap(role.getName()) + "(" + paramType + ")";

            fragmentMap.add(Parser.METHOD + ":" + signature, result, 3, role.getModified());


            if (role.getCardinality() != ClassModelBuilder.ONE) {
                st = group.getInstanceOf("withoutMethod");
                st.add("roleName", role.getName());
                st.add("toMany", role.getCardinality() != ClassModelBuilder.ONE);
                st.add("myClassName", clazz.getName());
                st.add("otherClassName", role.getOther().getClazz().getName());
                st.add("otherRoleName", role.getOther().getName());
                st.add("otherToMany", role.getOther().getCardinality() != ClassModelBuilder.ONE);
                st.add("roleType", roleType);
                result = st.render();

                fragmentMap.add(Parser.METHOD + ":without" + StrUtil.cap(role.getName()) + "(Object...)", result, 3, role.getModified());
            }
        }
    }


    private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap) {
        fragmentMap.add(Parser.IMPORT + ":java.beans.PropertyChangeSupport", "import java.beans.PropertyChangeSupport;", 1);
        fragmentMap.add(Parser.IMPORT + ":java.beans.PropertyChangeListener", "import java.beans.PropertyChangeListener;", 1);

        STGroup group = new STGroupFile("templates/propertyChangeSupport.stg");
        group.registerRenderer(String.class, new StringRenderer());

        String result = "   protected PropertyChangeSupport listeners = null;";
        fragmentMap.add(Parser.ATTRIBUTE + ":listeners", result, 2, clazz.getModified());

        ST st = group.getInstanceOf("firePropertyChange");
        result = st.render();
        fragmentMap.add(Parser.METHOD + ":firePropertyChange(String,Object,Object)", result, 2, clazz.getModified());

        st = group.getInstanceOf("addPropertyChangeListener1");
        result = st.render();
        fragmentMap.add(Parser.METHOD + ":addPropertyChangeListener(PropertyChangeListener)", result, 2, clazz.getModified());

        st = group.getInstanceOf("addPropertyChangeListener2");
        result = st.render();
        fragmentMap.add(Parser.METHOD + ":addPropertyChangeListener(String,PropertyChangeListener)", result, 2, clazz.getModified());

        st = group.getInstanceOf("removePropertyChangeListener1");
        result = st.render();
        fragmentMap.add(Parser.METHOD + ":removePropertyChangeListener(PropertyChangeListener)", result, 2, clazz.getModified());

        st = group.getInstanceOf("removePropertyChangeListener2");
        result = st.render();
        fragmentMap.add(Parser.METHOD + ":removePropertyChangeListener(String,PropertyChangeListener)", result, 2, clazz.getModified());
    }


    private void generateToString(Clazz clazz, FileFragmentMap fragmentMap) {
        ArrayList<String> nameList = new ArrayList<>();
        boolean modified = false;
        for (Attribute attr : clazz.getAttributes()) {
            if (attr.getType().equals(ClassModelBuilder.STRING)) {
                nameList.add(attr.getName());
            }

            if (attr.getModified() == true) {
                modified = true;
            }
        }

        String result = "";
        if (nameList.size() > 0) {
            STGroup group = new STGroupFile("templates/toString.stg");
            group.registerRenderer(String.class, new StringRenderer());
            ST st = group.getInstanceOf("toString");
            st.add("names", nameList.toArray(new String[0]));
            result = st.render();
        }

        fragmentMap.add(Parser.METHOD + ":toString()", result, 2, modified);
    }
}
