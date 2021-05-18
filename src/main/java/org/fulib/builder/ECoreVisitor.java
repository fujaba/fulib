package org.fulib.builder;

import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

class ECoreVisitor
{
   private static final Map<String, BiConsumer<ECoreVisitor, Element>> METHOD_MAP;
   private static final Map<String, String> TYPE_MAP;

   static
   {
      METHOD_MAP = new HashMap<>();
      METHOD_MAP.put("ecore:EPackage", ECoreVisitor::visitEPackage);
      METHOD_MAP.put("eClassifiers", ECoreVisitor::visitEClassifier);
      METHOD_MAP.put("eStructuralFeatures", ECoreVisitor::visitEStructuralFeature);
      METHOD_MAP.put("eLiterals", ECoreVisitor::visitELiteral);

      TYPE_MAP = new HashMap<>();
      TYPE_MAP.put("Int", Type.INT);
      TYPE_MAP.put("Double", Type.DOUBLE);
      TYPE_MAP.put("Float", Type.FLOAT);
      TYPE_MAP.put("Long", Type.LONG);
   }

   private final ClassModelManager m;
   private Clazz clazz;

   ECoreVisitor(ClassModelManager m)
   {
      this.m = m;
   }

   void load(String uri) throws Exception
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(uri);
      Element root = document.getDocumentElement();
      root.normalize();
      visit(root);
   }

   private void visit(Element root)
   {
      final String tagName = root.getTagName();
      final BiConsumer<ECoreVisitor, Element> visitFunction = METHOD_MAP.get(tagName);

      if (visitFunction != null)
      {
         visitFunction.accept(this, root);
      }
      else
      {
         Logger.getGlobal().severe(String.format("Don't know how to handle %s", tagName));
      }
   }

   private void visitELiteral(Element element)
   {
      clazz.setPropertyStyle(Type.POJO);
      String name = element.getAttribute("name");
      Attribute attribute = m.haveAttribute(clazz, name, Type.STRING, String.format("\"%s\"", name));
   }

   private void visitEStructuralFeature(Element element)
   {
      final String xsiType = element.getAttribute("xsi:type");
      final String name = element.getAttribute("name");
      String eType = element.getAttribute("eType");
      eType = eType.substring(eType.lastIndexOf("/") + 1);
      final String upperBound = element.getAttribute("upperBound");
      final int card = upperBound.isEmpty() ? Type.ONE : Type.MANY;

      if (xsiType.equals("ecore:EAttribute"))
      {
         final String attrType = TYPE_MAP.getOrDefault(eType, eType);
         final Attribute attribute = m.haveAttribute(clazz, name, attrType);
         if (card != Type.ONE)
         {
            attribute.setCollectionType(m.getClassModel().getDefaultCollectionType());
         }
      }
      else if (xsiType.equals("ecore:EReference"))
      {
         final Clazz otherClazz = m.haveClass(eType);
         final String containment = element.getAttribute("containment");
         String otherName = element.getAttribute("eOpposite");
         if ("true".equals(containment))
         {
            otherName = "parent";
            if (clazz.getRole(otherName) == null)
            {
               m.associate(clazz, name, card, otherClazz, otherName, Type.ONE);
            }
         }
         else if (otherName.isEmpty())
         {
            m.associate(clazz, name, card, otherClazz, null, 0);
         }
         else
         {
            otherName = otherName.substring(otherName.lastIndexOf('/') + 1);
            m.associate(clazz, name, card, otherClazz, otherName, 0);
         }
      }
      else
      {
         Logger.getGlobal().severe("unknown type for structural feature: " + xsiType);
      }
   }

   private void visitEClassifier(Element element)
   {
      String name = element.getAttribute("name");
      clazz = m.haveClass(name);

      String eSuperTypes = element.getAttribute("eSuperTypes");
      if (eSuperTypes.length() > 0)
      {
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

      for (int i = 0; i < childNodes.getLength(); i++)
      {
         Node node = childNodes.item(i);
         if (node instanceof Element)
         {
            visit((Element) node);
         }
         else if (node instanceof Text)
         {
            // ignore
         }
         else
         {
            Logger.getGlobal().severe("child is no element");
         }
      }
   }
}
