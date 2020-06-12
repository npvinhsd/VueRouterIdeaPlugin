import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptVariable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class VueRouteProvider {
    private final PsiElement element;


    public VueRouteProvider(PsiElement element) {
        this.element = element;
    }

    private boolean isSupportPsiElement() {
        if (this.element == null) {
            return false;
        }
        PsiElement parent = this.element.getParent();
        if (!(parent instanceof JSLiteralExpression) || parent.getParent() == null) {
            return false;
        }

        PsiElement propertyEl = this.element.getParent().getParent();

        if (propertyEl instanceof JSProperty) {
            String name = ((JSProperty) propertyEl).getName();
            return name != null && name.equals("name");
        }

        return false;
    }

    private boolean isRouteConfigType() {
        if (!this.isSupportPsiElement()) {
            return false;
        }

        PsiElement tsVariable = PsiTreeUtil.findFirstParent(
                this.element,
                element1 -> element1 instanceof TypeScriptVariable
        );

        if (tsVariable == null) {
            return false;
        }

        PsiElement type = PsiTreeUtil.findChildOfType(tsVariable, TypeScriptSingleType.class);

        if (type == null) {
            return false;
        }

        return VueRouterTsUtils.isRouteConfigType(type);
    }

    @NotNull
    public Collection<LookupElement> getLookupElements() {
        if (!this.isSupportPsiElement()) {
            return Collections.emptyList();
        }

        Collection<LookupElement> lookupElements = new ArrayList<>();

        if (this.isRouteConfigType()) {
            String name = RoutingUtils.getParentName(this.element);
            if (!name.isEmpty()) {
                lookupElements.add(LookupElementBuilder.create(name).withIcon(IconsManager.ROUTE));
            }
        } else {
            for (String s : RoutingUtils.getRoutesAsNames(this.element.getProject())) {
                lookupElements.add(LookupElementBuilder.create(s).withIcon(IconsManager.ROUTE));
            }
        }


        return lookupElements;
    }

    @NotNull
    public Collection<PsiElement> getPsiTargets() {
        if (!this.isSupportPsiElement()) {
            return Collections.emptyList();
        }
        String content = (String) ((JSLiteralExpression) this.element.getParent()).getValue();
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }

        return RoutingUtils.getRoutesAsTargets(element.getProject(), content);
    }

}
