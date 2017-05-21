package org.mockito.release.internal.gradle;

import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GitCommitTask extends Exec{

    private List<File> filesToCommit = new ArrayList<File>();
    private List<String> messages = new ArrayList<String>();

    public void addChange(List<File> files, String message, Task taskMakingChange){
        dependsOn(taskMakingChange);
        filesToCommit.addAll(files);
        messages.add(message);
    }

    public List<String> getFiles() {
        List<String> result = new ArrayList<String>();
        for(File file : filesToCommit){
            result.add(file.getAbsolutePath());
        }
        return result;
    }

    public String getAggregatedMessage(){
        StringBuilder result = new StringBuilder();
        for(String msg : messages){
            result.append(msg).append(" + ");
        }
        if(!messages.isEmpty()){
            result.delete(result.length() - 3, result.length());
        }
        return result.toString();
    }
}
