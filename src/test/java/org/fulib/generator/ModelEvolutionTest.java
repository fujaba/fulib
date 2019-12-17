package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Generator;
import org.fulib.Tools;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelEvolutionTest
{
   @Test
   void testModelEvolution() throws IOException
   {
      final String targetFolder = "tmp/model-evolution";
      final String srcFolder = targetFolder + "/src";
      final String outFolder = targetFolder + "/out";
      final String packageName = "org.evolve";

      Tools.removeDirAndFiles(targetFolder);

      // first simple model
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);
      ClassBuilder uni = mb.buildClass("University").buildAttribute("uniName", Type.STRING);
      ClassBuilder stud = mb.buildClass("Student").buildAttribute("matNo", Type.STRING)
                            .buildAttribute("startYear", Type.INT);
      uni.buildAssociation(stud, "students", Type.MANY, "uni", Type.ONE);
      ClassBuilder room = mb.buildClass("Room").buildAttribute("roomNo", Type.STRING);

      ClassModel firstModel = mb.getClassModel();

      UniversityFileHelper.create(packageName, firstModel);

      Fulib.generator().generate(firstModel);

      int compileResult = Tools.javac(outFolder, firstModel.getPackageSrcFolder());
      assertThat(compileResult, equalTo(0));
      assertThat(Files.exists(Paths.get(firstModel.getPackageSrcFolder() + "/University.java")), is(true));

      // rename an attribute
      uni.getClazz().setName("Institute");
      room.getClazz().setName("LectureHall");
      stud.getClazz().getAttribute("matNo").setName("studentId");
      stud.getClazz().getAttribute("startYear").setType(Type.STRING);

      // prepare logger
      Logger logger = Logger.getLogger(Generator.class.getName());
      final ArrayList<LogRecord> logRecordList = new ArrayList<>();
      Handler handler = new Handler()
      {
         @Override
         public void publish(LogRecord record)
         {
            logRecordList.add(record);
         }

         @Override
         public void flush()
         {
         }

         @Override
         public void close() throws SecurityException
         {
         }
      };
      logger.setUseParentHandlers(false);
      logger.addHandler(handler);
      logger.setLevel(Level.INFO);

      Fulib.generator().generate(firstModel);
      assertThat(logRecordList.size(), not(equalTo(0)));

      compileResult = Tools.javac(outFolder, firstModel.getPackageSrcFolder());
      assertThat(compileResult, equalTo(0));

      // rename a class
      uni.getClazz().setName("Institute");

      // TODO rename an association

   }
}
