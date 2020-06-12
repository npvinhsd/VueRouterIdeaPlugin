import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType;
import com.intellij.psi.PsiElement;

public class VueRouterTsUtils {
    public static boolean isRouteConfigType(PsiElement element) {
        if (!(element instanceof TypeScriptSingleType)) {
            return false;
        }
        String type = ((TypeScriptSingleType) element).getQualifiedTypeName();

        return type != null && type.equals("RouteConfig");
    }
}
