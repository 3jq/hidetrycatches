package dev.sl33py.hidetrycatches;

/**
 * @author yoursleep
 * @since 28 январь 2022
 */
import me.coley.recaf.control.Controller;
import me.coley.recaf.plugin.api.*;
import me.coley.recaf.workspace.Workspace;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.plugface.core.annotations.Plugin;

import java.util.HashMap;
import java.util.Map;

@Plugin(name = "HideTryCatches Plugin")
public class HideTryCatchesPlugin implements WorkspacePlugin, StartupPlugin{
    private Controller controller;
    private Workspace workspace;

    public void hideTryCatches() {
        if (workspace == null) return;
        HashMap<String, ClassNode> classes = new HashMap<>();

        for (Map.Entry<String, byte[]> entry : workspace.getPrimary().getClasses().entrySet()) {
            ClassReader reader = new ClassReader(entry.getValue());
            ClassNode classNode = new ClassNode();

            reader.accept(classNode, 0);

            classNode.methods.forEach(m -> m.tryCatchBlocks.clear());

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);

            workspace.getPrimary().getClasses().replace(entry.getKey(), classWriter.toByteArray());
        }
    }

    @Override public String getName() {
        return "HideTryCatches";
    }

    @Override public String getVersion() {
        return "1.0";
    }

    @Override public String getDescription() {
        return "A plugin for Recaf for hiding try catches";
    }


    @Override public void onClosed(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override public void onOpened(Workspace workspace) {
        this.workspace = workspace;

        hideTryCatches();
    }

    public Controller getController() {
        return controller;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Override public void onStart(Controller controller) {
        this.controller = controller;
        hideTryCatches();
    }
}
