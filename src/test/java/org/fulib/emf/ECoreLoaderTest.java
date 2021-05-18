package org.fulib.emf;

import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.emf.ECoreLoaderPlugin;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ECoreLoaderTest
{
   @Test
   void laboratyAutomation()
   {
      final String packageName = "org.fulib.test.laboratoryAutomation";

      ClassModelManager m = new ClassModelManager();
      m.setPackageName(packageName);

      m.apply(new ECoreLoaderPlugin(getClass().getResource("laboratoryAutomation.ecore").toString()));

      final ClassModel classModel = m.getClassModel();

      // Classes
      final Clazz assay = classModel.getClazz("Assay");
      final Clazz protocolStep = classModel.getClazz("ProtocolStep");
      final Clazz sample = classModel.getClazz("Sample");
      final Clazz jobRequest = classModel.getClazz("JobRequest");
      final Clazz addReagent = classModel.getClazz("AddReagent");

      assertThat("super types are set", addReagent.getSuperClass(), is(protocolStep));

      // Attributes
      final Attribute assayName = assay.getAttribute("name");
      assertThat("attribute types are kept", assayName.getType(), CoreMatchers.is(Type.STRING));

      final Attribute sampleState = sample.getAttribute("state");
      assertThat("custom attributes types are kept", sampleState.getType(), is("SampleState"));

      final Attribute addReagentVolume = addReagent.getAttribute("volume");
      assertThat("primitive attribute types are mapped", addReagentVolume.getType(), is(Type.DOUBLE));

      // Associations
      final AssocRole jobRequestAssay = jobRequest.getRole("assay");
      assertThat("1-1 containments work", jobRequestAssay.getCardinality(), is(Type.ONE));
      final AssocRole assayParent = assay.getRole("parent");
      assertThat("1-1 containments work", assayParent.getCardinality(), is(Type.ONE));
      assertThat("1-1 containments are linked", assayParent.getOther(), is(jobRequestAssay));

      final AssocRole jobRequestSamples = jobRequest.getRole("samples");
      assertThat("1-n containments work", jobRequestSamples.getCardinality(), is(Type.MANY));
      final AssocRole sampleParent = sample.getRole("parent");
      assertThat("1-n containments work", sampleParent.getCardinality(), is(Type.ONE));
      assertThat("1-n containments are linked", sampleParent.getOther(), is(jobRequestSamples));

      final AssocRole protocolStepNext = protocolStep.getRole("next");
      assertThat("1-1 self associations work", protocolStepNext.getCardinality(), is(Type.ONE));
      final AssocRole protocolStepPrevious = protocolStep.getRole("previous");
      assertThat("1-1 self associations work", protocolStepPrevious.getCardinality(), is(Type.ONE));
      assertThat("1-1 self associations are linked", protocolStepNext.getOther(), is(protocolStepPrevious));
   }

   @Test
   void jobCollection()
   {
      final String packageName = "org.fulib.test.jobCollection";

      ClassModelManager m = new ClassModelManager();
      m.setPackageName(packageName);

      m.apply(new ECoreLoaderPlugin(getClass().getResource("jobCollection.ecore").toString()));

      final ClassModel classModel = m.getClassModel();

      // Classes
      final Clazz tubeRunner = classModel.getClazz("TubeRunner");
      final Clazz job = classModel.getClazz("Job");

      // Attributes
      final Attribute tubeRunnerBarcodes = tubeRunner.getAttribute("barcodes");
      assertThat("collection attributes use the element type", tubeRunnerBarcodes.getType(), is(Type.STRING));
      assertThat("collection attributes have a collection type", tubeRunnerBarcodes.getCollectionType(), notNullValue());

      // Associations
      final AssocRole jobPrevious = job.getRole("previous");
      assertThat("n-n associations work", jobPrevious.getCardinality(), is(Type.MANY));
      final AssocRole jobNext = job.getRole("next");
      assertThat("n-n associations work", jobNext.getCardinality(), is(Type.MANY));
      assertThat("n-n associations are linked", jobNext.getOther(), is(jobPrevious));
   }
}
