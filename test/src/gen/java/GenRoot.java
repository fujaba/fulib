import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;

public class GenRoot implements ClassModelDecorator {
   @Override
   public void decorate(ClassModelManager mb) {
      mb.haveClass("RootExample");
   }
}
