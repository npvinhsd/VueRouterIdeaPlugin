import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

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

                        if (psiElement == null || psiElement.getParent() == null || psiElement.getParent().getParent() == null) {
                            return;
                        }
                        PsiElement propertyEl = psiElement.getParent().getParent();
                        RouteLogger.LOG.warn(propertyEl.getClass().toString());
                        if (propertyEl instanceof JSProperty) {
                            String name = ((JSProperty) propertyEl).getName();
                            if (name != null && name.equals("name")) {
                                VueRouteProvider provider = new VueRouteProvider(psiElement);
                                resultSet.addAllElements(provider.getLookupElements());
                            }

                        }
//                        FileBasedIndex.getInstance().scheduleRebuild(RouteIndexExtension.KEY, new Throwable("Refresh RouteIndexExtension"));
                    }
                }
        );
    }
}
