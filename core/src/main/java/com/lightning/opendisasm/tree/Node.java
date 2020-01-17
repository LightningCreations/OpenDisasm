package com.lightning.opendisasm.tree;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.lightning.opendisasm.util.StringUtil;

import java.util.List;

public interface Node {
	/**
	 * Returns the parent of this node.
	 * A Node may have no parent, represented by this method returning null.
	 */
	public @Nullable Node getParent();

	/**
	 * Gets all the Children of this node, or an empty list.
	 * For all Children c of this node, c.getParent().equals(this) shall be true.
	 */
	public @NonNull List<? extends @NonNull Node> getChildren();

	public @NonNull String getName();
	
	public default String stringify(int numSpaces) {
	    StringBuilder result = new StringBuilder();

        result.append(StringUtil.getSpaces(numSpaces)); result.append(getName()); result.append(": {\n");
        List<? extends @NonNull Node> children = getChildren();
        for(int i = 0; i < children.size(); i++) {
            Node n = children.get(i);
            result.append(n.stringify(numSpaces+2));
            if(i < children.size()-1) result.append(',');
            result.append('\n');
        }
        result.append(StringUtil.getSpaces(numSpaces)); result.append("}");
	    
	    return result.toString();
	}
	
	public default String stringify() {
	    return stringify(0);
	}
}
