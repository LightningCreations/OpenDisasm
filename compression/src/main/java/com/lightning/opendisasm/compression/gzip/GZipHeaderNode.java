package com.lightning.opendisasm.compression.gzip;

import com.lightning.opendisasm.tree.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class GZipHeaderNode implements Node {
    private int magic;
    private int compressionId;
    private int flg;
    private long mtime;
    private int xflg;
    private List<Node> extraFields;

    @Override
    public @Nullable Node getParent() {
        return null;
    }

    @Override
    public @NonNull List<? extends Node> getChildren() {
        return null;
    }

    @Override
    public @NonNull String getName() {
        return "GZip Header";
    }
}
