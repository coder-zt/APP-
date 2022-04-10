package com.coder.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Zip

public class ZipUtils extends DefaultTask{


    @TaskAction
    void doZip(){
        archiveName("res.zip")
        destinationDir file("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out")
        from("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out\\resources_arsc")
    }

}