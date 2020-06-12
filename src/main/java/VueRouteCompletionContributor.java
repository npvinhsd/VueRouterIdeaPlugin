import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class VueRouteCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {
    public VueRouteCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(JSTokenTypes.STRING_LITERAL),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        PsiElement psiElement = parameters.getOriginalPosition();

                        VueRouteProvider provider = new VueRouteProvider(psiElement);

                        Collection<LookupElement> lookups = provider.getLookupElements();
                        if (lookups.size() > 0) {
                            resultSet.addAllElements(provider.getLookupElements());
                            resultSet.stopHere();
                        }

//                        FileBasedIndex.getInstance().scheduleRebuild(RouteIndexExtension.KEY, new Throwable("Refresh RouteIndexExtension"));
                    }
                }
        );
    }
}
