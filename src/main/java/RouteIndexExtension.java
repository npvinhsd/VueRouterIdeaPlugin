import com.intellij.lang.javascript.TypeScriptFileType;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.intellij.util.io.VoidDataExternalizer;
import gnu.trove.THashMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class RouteIndexExtension extends FileBasedIndexExtension<String, Void> {
    public static final ID<String, Void> KEY = ID.create("me.kapien.vue_routes");

    @Override
    public @NotNull ID<String, Void> getName() {
        return KEY;
    }

    @Override
    public @NotNull DataIndexer<String, Void, FileContent> getIndexer() {
        return fileContent -> {

            PsiFile psiFile = fileContent.getPsiFile();

            if (!isValidFileName(fileContent)) {
                return Collections.emptyMap();
            }

            Map<String, Void> map = new THashMap<>();

            for (String s : RoutingUtils.getRoutesAsNames(psiFile)) {
                map.put(s, null);
            }

            return map;
        };

    }

    private boolean isValidFileName(FileContent psiFile) {
        return StringUtils.containsIgnoreCase(psiFile.getFile().getCanonicalPath(), "route")
                && StringUtils.endsWith(psiFile.getFileName(), ".ts");
    }

    @Override
    public @NotNull KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public @NotNull DataExternalizer<Void> getValueExternalizer() {
        return VoidDataExternalizer.INSTANCE;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public FileBasedIndex.@NotNull InputFilter getInputFilter() {
        return file -> (file.getFileType() == TypeScriptFileType.INSTANCE
                || (file.getExtension() != null && file.getExtension().equals("vue"))) && (!file.getCanonicalPath().contains("node_modules"));
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
