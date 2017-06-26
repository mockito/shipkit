package org.shipkit.internal.gradle;

import org.gradle.api.Task;
import org.shipkit.gradle.exec.CompositeExecTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Commits all changes registered with {@link GitCommitTask#addChange} method
 * Commit message is concatenated from all descriptions of registered changes
 */
public class GitCommitTask extends CompositeExecTask{

    private List<File> filesToCommit = new ArrayList<File>();
    private List<String> descriptions = new ArrayList<String>();

    public void addChange(List<File> files, String changeDescription, Task taskMakingChange){
        dependsOn(taskMakingChange);
        filesToCommit.addAll(files);
        descriptions.add(changeDescription);
    }

    public List<String> getFiles() {
        List<String> result = new ArrayList<String>();
        for(File file : filesToCommit){
            result.add(file.getAbsolutePath());
        }
        return result;
    }

    public String getAggregatedCommitMessage(){
        StringBuilder result = new StringBuilder();
        for(String msg : descriptions){
            result.append(msg).append(" + ");
        }
        if(!descriptions.isEmpty()){
            result.delete(result.length() - 3, result.length());
        }
        return result.toString();
    }
}
