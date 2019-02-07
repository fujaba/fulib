package org.fulib.util;

import org.fulib.Parser;
import org.fulib.StrUtil;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.ArrayList;

public class Generator4ClassFile extends FileGenerator {

    @Override
    public void generate(Clazz clazz) {
        String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
        FileFragmentMap fragmentMap = Parser.parse(classFileName);

        // doGenerate code for class
        generatePackageDecl(clazz, fragmentMap);

        generateClassDecl(clazz, fragmentMap);

        generateAttributes(clazz, fragmentMap);

        generateAssociations(clazz, fragmentMap);

        generatePropertyChangeSupport(clazz, fragmentMap);

        generateToString(clazz, fragmentMap);

        generateRemoveYou(clazz, fragmentMap);

        fragmentMap.add(Parser.CLASS_END, "}", 1);

        writeClassOrDeleteIfEmpty(fragmentMap, clazz, classFileName);
    }

    public void generateTest(String outputDir, Clazz clazz) {

        String testClassFileName = outputDir + "/" + clazz.getModel().getPackageName()
                .replaceAll("\\.", "/") + "/Test" + clazz.getName() + ".java";

        FileFragmentMap fragmentMap = new FileFragmentMap(testClassFileName);

        generatePackageDecl(clazz, fragmentMap);
        generateClassDecl(clazz, fragmentMap);
//        generateAttributeTests(clazz, fragmentMap);
//        generateAssociationTests(clazz, fragmentMap);
//        generatePropertyChangeSupportTests(clazz, fragmentMap);
//        generateToStringTests(clazz, fragmentMap);
//        generateRemoveYouTests(clazz, fragmentMap);

        fragmentMap.add(Parser.CLASS_END, "}", 1);

        writeClassOrDeleteIfEmpty(fragmentMap, clazz, testClassFileName, true);
    }

    private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap) {
        String result = String.format("package %s;", clazz.getModel().getPackageName());
        fragmentMap.add(Parser.PACKAGE, result, 2);
    }

    private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap) {
        STGroup group = createSTGroup("templates/classDecl.stg");
        ST st = group.getInstanceOf("classDecl");
        st.add("name", clazz.getName());
        st.add("superClass", clazz.getSuperClass() != null ? clazz.getSuperClass().getName() : null);
        String result = st.render();
        fragmentMap.add(Parser.CLASS, result, 2);
    }

    private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap) {
        STGroup group;
        ST attrTemplate;
        String result;

        for (Attribute attr : clazz.getAttributes()) {
            if (ClassModelBuilder.JAVA_FX.equals(attr.getPropertyStyle())) {
                group = createSTGroup("templates/JavaFXattributes.stg");
                fragmentMap.add(Parser.IMPORT + ":javafx.beans.property.*", "import javafx.beans.property.*;", 1);
            } else {
                group = createSTGroup("templates/attributes.stg");
            }

            String attrType = attr.getType();
            if (ClassModelBuilder.JAVA_FX.equals(attr.getPropertyStyle())) {
                if (attrType.equals("int")) {
                    attrType = "Integer";
                } else {
                    attrType = StrUtil.cap(attrType);
                }
            }
            attrTemplate = group.getInstanceOf("propertyDecl");
            attrTemplate.add("name", attr.getName());
            result = attrTemplate.render();
            fragmentMap.add(Parser.ATTRIBUTE + ":PROPERTY_" + attr.getName(), result, 2, attr.getModified());


            attrTemplate = group.getInstanceOf("attrDecl");
            attrTemplate.add("type", attrType);
            attrTemplate.add("name", attr.getName());
            attrTemplate.add("value", attr.getInitialization());
            result = attrTemplate.render();

            fragmentMap.add(Parser.ATTRIBUTE + ":" + attr.getName(), result, 2, attr.getModified());


            if (ClassModelBuilder.JAVA_FX.equals(attr.getPropertyStyle())) {
                attrTemplate = group.getInstanceOf("initMethod");
                attrTemplate.add("name", attr.getName());
                attrTemplate.add("type", attrType);
                result = attrTemplate.render();

                fragmentMap.add(Parser.METHOD + ":_init" + StrUtil.cap(attr.getName()) + "()", result, 2, attr.getModified());
            } else {
                fragmentMap.add(Parser.METHOD + ":_init" + StrUtil.cap(attr.getName()) + "()", "", 2, true);
            }

            attrTemplate = group.getInstanceOf("attrGet");
            attrTemplate.add("type", attr.getType());
            attrTemplate.add("name", attr.getName());
            result = attrTemplate.render();

            fragmentMap.add(Parser.METHOD + ":get" + StrUtil.cap(attr.getName()) + "()", result, 2, attr.getModified());


            attrTemplate = group.getInstanceOf("attrSet");
            attrTemplate.add("class", attr.getClazz().getName());
            attrTemplate.add("type", attr.getType());
            attrTemplate.add("name", attr.getName());
            attrTemplate.add("useEquals", attr.getType().equals("String"));
            result = attrTemplate.render();

            fragmentMap.add(Parser.METHOD + ":set" + StrUtil.cap(attr.getName()) + "(" + attr.getType() + ")", result, 3, attr.getModified());

            if (ClassModelBuilder.JAVA_FX.equals(attr.getPropertyStyle())) {
                attrTemplate = group.getInstanceOf("propertyGet");
                attrTemplate.add("name", attr.getName());
                attrTemplate.add("type", attrType);
                result = attrTemplate.render();

                fragmentMap.add(Parser.METHOD + ":" + attr.getName() + "Property()", result, 3, attr.getModified());
            } else {
                fragmentMap.add(Parser.METHOD + ":" + attr.getName() + "Property()", "", 3, true);
            }
        }
    }

    private void generateAssociations(Clazz clazz, FileFragmentMap fragmentMap) {
        STGroup group;

        String result;
        ST st;
        for (AssocRole role : clazz.getRoles()) {
            if (role.getName() == null) {
                continue; //=====================================
            }

            if (ClassModelBuilder.JAVA_FX.equals(role.getPropertyStyle())) {
                group = createSTGroup("templates/JavaFXassociations.stg");
                fragmentMap.add(Parser.IMPORT + ":javafx.beans.property.*", "import javafx.beans.property.*;", 1);
            } else {
                group = createSTGroup("templates/associations.stg");
            }

            String roleType = role.getOther().getClazz().getName();

            // provide empty_set in this class
            if (role.getCardinality() != ClassModelBuilder.ONE
                    && !ClassModelBuilder.JAVA_FX.equals(role.getPropertyStyle())) {
                // add empty set decl
                roleType = String.format(role.getRoleType(), role.getOther().getClazz().getName());

                st = group.getInstanceOf("emptySetDecl");
                st.add("roleName", role.getName());
                st.add("otherClassName", role.getOther().getClazz().getName());
                st.add("roleType", roleType);
                result = st.render();

                fragmentMap.add(Parser.ATTRIBUTE + ":EMPTY_" + role.getName(), result, 3, role.getModified());
            }


            st = group.getInstanceOf("propertyDecl");
            st.add("roleName", role.getName());
            result = st.render();
            fragmentMap.add(Parser.ATTRIBUTE + ":PROPERTY_" + role.getName(), result, 2, role.getModified());


            st = group.getInstanceOf("roleAttrDecl");
            st.add("roleName", role.getName());
            st.add("roleType", roleType);
            st.add("toMany", role.getCardinality() != ClassModelBuilder.ONE);
            st.add("otherClassName", role.getOther().getClazz().getName());
            result = st.render();

            fragmentMap.add(Parser.ATTRIBUTE + ":" + role.getName(), result, 2, role.getModified());

            if (ClassModelBuilder.JAVA_FX.equals(role.getPropertyStyle())) {
                // remove empty set decl
                result = "";
                fragmentMap.add(Parser.ATTRIBUTE + ":EMPTY_" + role.getName(), result, 3, true);

                // add _init method
                st = group.getInstanceOf("initMethod");
                st.add("roleName", role.getName());
                st.add("toMany", role.getCardinality() != ClassModelBuilder.ONE);
                st.add("myClassName", clazz.getName());
                st.add("otherClassName", role.getOther().getClazz().getName());
                st.add("otherRoleName", role.getOther().getName());
                st.add("otherToMany", role.getOther().getCardinality() != ClassModelBuilder.ONE);
                result = st.render();

                fragmentMap.add(Parser.METHOD + ":_init" + StrUtil.cap(role.getName()) + "()", result, 2, role.getModified());
            } else {
                // remove _init method
                fragmentMap.add(Parser.METHOD + ":_init" + StrUtil.cap(role.getName()) + "()", "", 2, true);
            }


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
            if (ClassModelBuilder.JAVA_FX.equals(role.getPropertyStyle())) {
                if (role.getCardinality() != ClassModelBuilder.ONE) {
                    // remove withXY(Object...) method
                    String oldSignature = "with" + StrUtil.cap(role.getName()) + "(" + paramType + ")";
                    fragmentMap.add(Parser.METHOD + ":" + oldSignature, "", 3, true);
                }
                paramType = role.getOther().getClazz().getName();
            } else {
                // remove withXY(OtherClass)
                String oldSignature = "with" + StrUtil.cap(role.getName()) + "(" + role.getOther().getClazz().getName() + ")";
                fragmentMap.add(Parser.METHOD + ":" + oldSignature, "", 3, true);
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

                paramType = "Object...";
                if (ClassModelBuilder.JAVA_FX.equals(role.getPropertyStyle())) {
                    paramType = role.getOther().getClazz().getName();
                }
                fragmentMap.add(Parser.METHOD + ":without" + StrUtil.cap(role.getName()) + "(" + paramType + ")", result, 3, role.getModified());
            }

            if (ClassModelBuilder.JAVA_FX.equals(role.getPropertyStyle())
                    && role.getCardinality() == ClassModelBuilder.ONE) {
                st = group.getInstanceOf("propertyMethod");
                st.add("roleName", role.getName());
                st.add("otherClassName", role.getOther().getClazz().getName());
                result = st.render();

                fragmentMap.add(Parser.METHOD + ":" + role.getName() + "Property()", result, 3, role.getModified());
            } else {
                fragmentMap.add(Parser.METHOD + ":" + role.getName() + "Property()", "", 3, true);
            }
        }
    }

    private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap) {
        fragmentMap.add(Parser.IMPORT + ":java.beans.PropertyChangeSupport", "import java.beans.PropertyChangeSupport;", 1);
        fragmentMap.add(Parser.IMPORT + ":java.beans.PropertyChangeListener", "import java.beans.PropertyChangeListener;", 1);

        STGroup group = createSTGroup("templates/propertyChangeSupport.stg");

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
            STGroup group = createSTGroup("templates/toString.stg");
            ST st = group.getInstanceOf("toString");
            st.add("names", nameList.toArray(new String[0]));
            result = st.render();
        }

        fragmentMap.add(Parser.METHOD + ":toString()", result, 2, modified);
    }

    private void generateRemoveYou(Clazz clazz, FileFragmentMap fragmentMap) {
        ArrayList<String> toOneList = new ArrayList<>();
        ArrayList<String> toManyList = new ArrayList<>();
        ArrayList<String> toOneAggregationList = new ArrayList<>();
        ArrayList<String> toManyAggregationList = new ArrayList<>();
        ArrayList<String> toManyTypes = new ArrayList<>();
        ArrayList<Boolean> javaFXStyles = new ArrayList<>();
        boolean modified = clazz.getModified();

        for (AssocRole role : clazz.getRoles()) {
            if (role.getName() == null) {
                continue; //=============================
            }

            if (role.getCardinality() == ClassModelBuilder.ONE) {
                if (role.getAggregation() == true) {
                    toOneAggregationList.add(role.getName());
                } else {
                    toOneList.add(role.getName());
                }
            } else {
                if (role.getAggregation() == true) {
                    toManyAggregationList.add(role.getName());
                    toManyTypes.add(role.getOther().getClazz().getName());
                } else {
                    toManyList.add(role.getName());
                    javaFXStyles.add(ClassModelBuilder.JAVA_FX.equals(role.getPropertyStyle()));
                }
            }
        }

        String result = "";
        STGroup group = createSTGroup("templates/removeYou.stg");
        ST st = group.getInstanceOf("removeYou");
        st.add("toOneNames", toOneList.toArray(new String[0]));
        st.add("toManyNames", toManyList.toArray(new String[0]));
        st.add("toOneAggregations", toOneAggregationList.toArray(new String[0]));
        st.add("toManyAggregations", toManyAggregationList.toArray(new String[0]));
        st.add("toManyTypes", toManyTypes.toArray(new String[0]));
        st.add("javaFXStyles", javaFXStyles.toArray(new Boolean[0]));
        result = st.render();

        fragmentMap.add(Parser.METHOD + ":removeYou()", result, 2, modified);
    }
}
