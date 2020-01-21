package com.lightning.opendisasm.selftest;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SelfTestPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        SelfTestTask task = new SelfTestTask();
    }
}
