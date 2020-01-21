package com.lightning.opendisasm.selftest;

import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SelfTestTask extends JavaExec {
    private SourceSet testObjects;
    private SourceSet classes;
    public SelfTestTask(){

    }


    public SourceSet getClasses(){
        return this.classes;
    }
    public void setClasses(SourceSet classes){
        this.classes = classes;
    }
    public SourceSet getTestObjects(){
        return testObjects;
    }
    public void setTestObjects(SourceSet testObjects){
        this.testObjects = testObjects;
    }
    public void exec(){
        this.classpath(testObjects.getResources(),testObjects.getRuntimeClasspath(),classes.getRuntimeClasspath());
        this.setMain("com.lightning.opendisasm.selftest.start.Main");
        this.setArgs(testObjects.getResources().getSrcDirs().stream().flatMap(f-> f.isDirectory()?Arrays.stream(Objects.requireNonNull(f.list())): Stream.of(f.getName())).collect(Collectors.toList()));
        super.exec();
    }
}
