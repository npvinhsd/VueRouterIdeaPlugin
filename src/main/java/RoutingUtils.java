import com.intellij.lang.javascript.TypeScriptFileType;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RoutingUtils {
    public static Collection<String> getRoutesAsNames(Project project) {
        Collection<String> keys = FileBasedIndex.getInstance().getAllKeys(RouteIndexExtension.KEY, project);
        Set<String> set = new HashSet<>();

        for (String key : keys) {
            Collection fileCollection = FileBasedIndex.getInstance().getContainingFiles(
                    RouteIndexExtension.KEY,
                    key,
                    GlobalSearchScope.allScope(project)
            );
            if (fileCollection.size() > 0) {
                set.add(key);
            }
        }
        return set;
    }

    public interface RouteAsNameVisitor {
        void visit(@NotNull PsiElement element, @NotNull String name);
    }

    public static Collection<String> getRoutesAsNames(PsiFile file) {

        final Set<String> names = new HashSet<>();

        visitRoutesForAs(file, (element, name) ->
                names.add(name)
        );

        return names;
    }

    public static Collection<PsiElement> getRoutesAsTargets(@NotNull Project project, @NotNull String routeName) {
        Set<PsiElement> targets = new HashSet<>();
        Set<VirtualFile> virtualFiles = new HashSet<>();


        FileBasedIndex.getInstance().getFilesWithKey(
                RouteIndexExtension.KEY,
                Collections.singleton(routeName),
                virtualFile -> {
                    virtualFiles.add(virtualFile);
                    return true;
                },
                GlobalSearchScope.getScopeRestrictedByFileTypes(
                        GlobalSearchScope.allScope(project),
                        TypeScriptFileType.INSTANCE
                )
        );

        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (file != null) {
                targets.addAll(getRoutesAsTargets(file, routeName));
            }
        }

        return targets;
    }

    public static Collection<PsiElement> getRoutesAsTargets(@NotNull PsiFile psiFile, final @NotNull String routeName) {
        final Set<PsiElement> names = new HashSet<>();

        visitRoutesForAs(psiFile, (psiElement, name) -> {
            if (name.equals(routeName)) {
                names.add(psiElement);
            }
        });

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
        public void visitElement(@NotNull PsiElement element) {
            TypeScriptSingleType type = PsiTreeUtil.findChildOfType(element, TypeScriptSingleType.class);

            if (type != null && type.getQualifiedTypeName() != null && type.getQualifiedTypeName().equals("RouteConfig")) {
                Collection<JSObjectLiteralExpression> jsObjects = PsiTreeUtil.findChildrenOfType(element, JSObjectLiteralExpression.class);

                for (JSObjectLiteralExpression jsObject : jsObjects) {
                    if (jsObject != null) {
                        this.visitObject(jsObject);
                    }
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
                    if (value != null) {
                        this.visitor.visit(jsObject, value);
                    }
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
