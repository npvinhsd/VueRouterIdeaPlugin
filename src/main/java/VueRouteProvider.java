import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class VueRouteProvider {
    private final PsiElement element;


    public VueRouteProvider(PsiElement element) {
        this.element = element;
    }

    @NotNull
    public Collection<LookupElement> getLookupElements() {
        Collection<LookupElement> lookupElements = new ArrayList<>();
        for (String s : VueRouteProvider.getRoutesAsNames(this.element.getProject())) {
            lookupElements.add(LookupElementBuilder.create(s));
        }
        return lookupElements;
    }

    public static Collection<String> getRoutesAsNames(Project project) {
        Collection<String> keys = FileBasedIndex.getInstance().getAllKeys(RouteIndexExtension.KEY, project);

        RouteLogger.LOG.warn(keys.toString());

        return keys;
    }

    public interface RouteAsNameVisitor {
        void visit(@NotNull String name);
    }

    public static Collection<String> getRoutesAsNames(PsiFile file) {

        final Set<String> names = new HashSet<>();

        visitRoutesForAs(file, (name) ->
                names.add(name)
        );

        return names;
    }

    public static void visitRoutesForAs(@NotNull PsiFile psiFile, @NotNull RouteAsNameVisitor visitor) {
        psiFile.acceptChildren(new RouteNamePsiRecursiveElementVisitor(visitor));
    }


    private static class RouteNamePsiRecursiveElementVisitor extends PsiRecursiveElementVisitor {
        @NotNull
        private final RouteAsNameVisitor visitor;

        public RouteNamePsiRecursiveElementVisitor(@NotNull RouteAsNameVisitor visitor) {
            this.visitor = visitor;
        }

        @Override
        public void visitElement(PsiElement element) {
            TypeScriptSingleType type = PsiTreeUtil.findChildOfType(element, TypeScriptSingleType.class);

            if (type != null && type.getQualifiedTypeName() != null && type.getQualifiedTypeName().equals("RouteConfig")) {
                JSObjectLiteralExpression jsObject = PsiTreeUtil.findChildOfType(element, JSObjectLiteralExpression.class);

                if (jsObject != null) {
                    this.visitObject(jsObject);
                }
            }

            super.visitElement(element);
        }

        private void visitObject(JSObjectLiteralExpression jsObject) {
            JSProperty name = jsObject.findProperty("name");
            if (name != null && name.getValue() != null) {
                try {
                    JSLiteralExpression literalExpression = (JSLiteralExpression) name.getValue();
                    String value = (String) literalExpression.getValue();
                    this.visitor.visit(value);
                } catch (Exception e) {
                    RouteLogger.LOG.error(e);
                }
            }

            JSProperty children = jsObject.findProperty("children");

            if (children != null && children.getValue() != null) {
                JSArrayLiteralExpression childValue = ((JSArrayLiteralExpression) children.getValue());
                for (PsiElement child : childValue.getChildren()) {
                    if (child instanceof JSObjectLiteralExpression) {
                        this.visitObject((JSObjectLiteralExpression) child);
                    }
                }
            }
        }
    }
}
