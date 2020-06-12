import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class VueRouterGoToHandler implements GotoDeclarationHandler {
    private final static @NotNull Logger LOG = Logger.getInstance(VueRouterGoToHandler.class);

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement sourceElement, int offset, Editor editor) {
        Collection<PsiElement> psiTargets;

        VueRouteProvider provider = new VueRouteProvider(sourceElement);

        psiTargets = provider.getPsiTargets();

        return psiTargets.toArray(new PsiElement[psiTargets.size()]);
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return null;
    }
}
