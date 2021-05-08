package org.fulib.builder;

import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.w3c.dom.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class ECoreVisitor
{
   private ClassModelManager m;
   private Clazz clazz;

   public ECoreVisitor setClassModelManager(ClassModelManager m)
   {
      this.m = m;
      return this;
   }

   Map<String, Consumer<Element>> methodMap = null;

   public void visit(Element root)
   {
      initMethodMap();
      String tagName = root.getTagName();

      Consumer<Element> visitFunction = methodMap.get(tagName);

      if (visitFunction != null) {
         visitFunction.accept(root);
      }
      else {
         Logger.getGlobal().severe(String.format("Don't know how to handle %s", tagName));
      }
   }

   private void initMethodMap()
   {
      if (methodMap == null) {
         methodMap = new LinkedHashMap<>();
         methodMap.put("ecore:EPackage", this::visitEPackage);
         methodMap.put("eClassifiers", this::visitEClassifier);
         methodMap.put("eStructuralFeatures", this::visitEStructuralFeature);
         methodMap.put("eLiterals", this::visitELiteral);
      }
   }

   private void visitELiteral(Element element)
   {
      clazz.setPropertyStyle(Type.POJO);
      String name = element.getAttribute("name");
      Attribute attribute = m.haveAttribute(clazz, name, Type.STRING, String.format("\"%s\"", name));
   }

   LinkedHashMap<String, String> typeMap = null;

   private void visitEStructuralFeature(Element element)
   {
      initTypeMap();
      String xsiType = element.getAttribute("xsi:type");
      String name = element.getAttribute("name");
      String eType = element.getAttribute("eType");
      eType = eType.substring(eType.lastIndexOf("/") + 1);
      String upperBound = element.getAttribute("upperBound");
      upperBound = upperBound.isEmpty() ? "-1" : "-n";

      if (xsiType.equals("ecore:EAttribute")) {
         if ("Int Double String".indexOf(eType) < 0) {
            eType = "String";
         }
         if (upperBound.equals("-n")) {
            clazz.withImports("java.util.ArrayList;");
         }

         String attrType = typeMap.get(eType + upperBound);
         if (attrType == null) {
            Logger.getGlobal().severe("Don't know how to implement attribute type " + eType);
         }
         m.haveAttribute(clazz, name, attrType);
      }
      else if (xsiType.equals("ecore:EReference")) {
         Clazz otherClazz = m.haveClass(eType);
         String containment = element.getAttribute("containment");
         String otherName = element.getAttribute("eOpposite");
         if (containment.equals("true")) {
            otherName = "parent";
            int card = upperBound.equals("-1") ? Type.ONE : Type.MANY;
            if (clazz.getRole(otherName) == null) {
               m.associate(clazz, name, card, otherClazz, otherName, Type.ONE);
            }
         }
         else if (otherName.isEmpty()) {
            int card = upperBound.equals("-1") ? Type.ONE : Type.MANY;
            m.associate(clazz, name, card, otherClazz, null, card);
         }
         else {
            int card = upperBound.equals("-1") ? Type.ONE : Type.MANY;
            String[] split = otherName.split("/+");
            otherName = split[2];

            int otherCard = otherClazz.getRole(otherName) != null
               ? otherClazz.getRole(otherName).getCardinality()
               : Type.MANY;
            m.associate(clazz, name, card, otherClazz, otherName, otherCard);
         }
      }
      else {
         Logger.getGlobal().severe("unknown type for structural feature: " + xsiType);
      }
   }

   private void initTypeMap()
   {
      if (typeMap == null) {
         typeMap = new LinkedHashMap<>();
         typeMap.put("Int-1", "int");
         typeMap.put("Int-n", "ArrayList<Integer>");
         typeMap.put("Double-1", "double");
         typeMap.put("Double-n", "ArrayList<Double>");
         typeMap.put("String-1", "String");
         typeMap.put("String-n", "ArrayList<String>");
      }
   }

   private void visitEClassifier(Element element)
   {
      String name = element.getAttribute("name");
      clazz = m.haveClass(name);


      String eSuperTypes = element.getAttribute("eSuperTypes");
      if (eSuperTypes.length() > 0) {
         eSuperTypes = eSuperTypes.substring("#//".length());
         Clazz superClass = m.haveClass(eSuperTypes);
         m.haveSuper(clazz, superClass);
      }

      visitChildNodes(element);
   }

   private void visitEPackage(Element element)
   {
      visitChildNodes(element);
   }

   private void visitChildNodes(Element element)
   {
      NodeList childNodes = element.getChildNodes();

      for (int i = 0; i < childNodes.getLength(); i++) {
         Node node = childNodes.item(i);
         if (node instanceof Element) {
            visit((Element) node);
         }
         else if (node instanceof Text) {
            // ignore
         }
         else {
            Logger.getGlobal().severe("child is no element");
         }
      }
   }

}
