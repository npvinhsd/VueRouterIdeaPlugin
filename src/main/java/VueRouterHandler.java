//import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
//import com.intellij.openapi.actionSystem.DataContext;
//import com.intellij.openapi.diagnostic.Logger;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.psi.PsiElement;
//import org.apache.log4j.Level;
//import org.jetbrains.annotations.Nls;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//public class VueRouterHandler implements GotoDeclarationHandler {
//    private final static @NotNull Logger LOG = Logger.getInstance(VueRouterHandler.class);
//    private static VueRouteAutoCompletionRegistrar registrar = new VueRouteAutoCompletionRegistrar();
//    static {
//        LOG.setLevel(Level.DEBUG);
//    }
//
//    @Nullable
//    @Override
//    public PsiElement[] getGotoDeclarationTargets(PsiElement sourceElement, int offset, Editor editor) {
//        Collection<PsiElement> psiTargets = new ArrayList<PsiElement>();
//        PsiElement parent = sourceElement.getParent();
//        LOG.error("TEST TEST");
//        return new PsiElement[0];
//    }
//
//    @Override
//    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
//        return null;
//    }
//}
