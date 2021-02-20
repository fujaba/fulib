import org.fulib.FulibTools;
import org.fulib.tools.CodeFragments;

public class UpdateCodeFragments
{
   // TODO this would be better as a test that automatically runs
   public static void main(String[] args)
   {
      final CodeFragments fragments = FulibTools.codeFragments();
      fragments.load("test/");
      fragments.write("README.md", "doc/");
   }
}
