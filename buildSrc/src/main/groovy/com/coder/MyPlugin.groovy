package com.coder

import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.internal.res.namespaced.ProcessAndroidAppResourcesTask
import com.coder.plugin.arsc.ArscEditor
import com.coder.plugin.ulits.CompactAlgorithm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.file.FileTree
import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration
import org.gradle.api.internal.file.DefaultFilePropertyFactory
import org.gradle.api.tasks.bundling.Zip

class MyPlugin implements Plugin<Project> {

    Project mProject
    String  resourcesTempDir = "F:\\GradleTest"
    @Override
    void apply(Project project) {
        mProject = project
        project.afterEvaluate {
            hookAapt()
        }
//        project.task('testPlugin').doLast{
//            println project.pluginSrc.message
//        }
//        ProcessAndroidResources
        //文件大小：209k
//        def arscFile = new File("F:\\OwnerProject\\TestSmallGradle1.2\\buildSrc\\src\\main\\assets\\demo\\resources.arsc")
//        def arscEditor = new ArscEditor(arscFile)
//        arscEditor.readTable()
//        arscEditor.reset(0x7a, [:])
//        t.package.id = 1
//        arscEditor.writeTable(t)
//        arscEditor.show()
//        println "t ===> $t.typeList"
//        project.afterEvaluate() {
//            def preBuild = project.tasks['preBuild']
//            preBuild.doFirst {
//                println 'hookPreReleaseBuild'
//            }
//            preBuild.doLast {
//                println 'hookPreReleaseBuild2'
//            }
//        }
    }

    def hookAapt(){

//        mergeDebugResources
        mProject.tasks.each {
            if(it instanceof  LinkApplicationAndroidResourcesTask && it.name=="processDebugResources"){
                println(it.name)

                LinkApplicationAndroidResourcesTask resourcesTask = (LinkApplicationAndroidResourcesTask)it
                resourcesTask.doLast { LinkApplicationAndroidResourcesTask task ->

                    task.resPackageOutputFolder.each {
                        it.asFileTree.each {
//                            println(it.absolutePath)
                            if(it.name == "resources-debug.ap_") {
                                def parentPath = it.parentFile.absolutePath
                                def resourcesPath = parentPath + "\\resources_arsc"
                                def resourceFile = new File(resourcesPath)
                                if (!resourceFile.exists()) {
                                    resourceFile.mkdir()
                                }
                                FileTree resources = mProject.zipTree(it)
                                println("parentPath == " + parentPath)
                                resources.each {
                                    def that = it
                                    def fileHZ = that.absolutePath.split("resources-debug.ap__a74121b90b78eaa80c8255a7c18c1884")[1]
                                    println(" that.absolutePath " + that.absolutePath)
                                    def resourcePath = new File(resourceFile.absolutePath + fileHZ).parent
                                    mProject.copy {
                                        from that.absolutePath
                                        into resourcePath
                                    }
                                    if (it.name == "resources.arsc") {
                                        def arscFile = new File(resourceFile.absolutePath + fileHZ)
                                        def arscEditor = new ArscEditor(arscFile)
//                                        arscEditor.readTable()
                                        arscEditor.reset(0x7a, [:])
                                        println("修改文件")
                                    }
                                }//        archiveName "res.zip"
//        destinationDir file("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out")
//        from("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out\\resources_arsc")
                                File f = new File("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out\\resources_arsc");
                                new CompactAlgorithm(new File("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out", "resources-debug.ap_")).zipFiles(f);
                            }
                        }
                    }
                }
            }
//            it.doFirst { task ->
//                println(task.name + "====" + task.class)
//                task.inputs.files.each {
//                    File file = new File(it.absolutePath)
//                    printFiles(file)
//                }
//            }
//            it.doLast { task ->
//                println(task.name + "====" + task.class)
//                task.outputs.files.each {
//                    File file = new File(it.absolutePath)
//                   printFiles(file)
//                }
//            }
        }


    }

    //task zipFile(type:Zip){
    //    archiveName "res.zip"
    //    destinationDir file("")
    //    from("")
    //}
    def zipFiles(){
//        Zip zip = Zip.
//        zip.archiveName = "res.zip"
//        zip.destinationDir = new File("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out")
//        zip.from("E:\\Coding\\Android\\AppGradlePlugin\\app\\build\\intermediates\\processed_res\\debug\\out\\resources_arsc")
//        zip.execute()
    }


    def printFiles(File file){
        if(file.isDirectory()){
            file.listFiles().each {
                printFiles(it)
            }
        }else{
            println(file.absolutePath)
        }
    }

    /**
     * 准备资源类型及资源id映射中保留包切片
     * Prepare retained resource types and resource id maps for package slicing
     */
    protected void prepareSplit() {
        def idsFile = new File(resourcesTempDir + "//R.txt")
        println idsFile.absolutePath
        if (!idsFile.exists()) return
        println idsFile.name
        // Check if has any vendor aars
        def firstLevelVendorAars = [] as Set<ResolvedDependency>
        def transitiveVendorAars = [] as Set<Map>
        collectVendorAars(firstLevelVendorAars, transitiveVendorAars)
    }

    /**
     * 收集当前app包的依赖的正在编译的aar
     */
    /** Collect the vendor aars (has resources) compiling in current bundle */
    protected void collectVendorAars(Set<ResolvedDependency> outFirstLevelAars,
                                     Set<Map> outTransitiveAars) {
        println mProject.configurations.complie.resolvedConfiguration.firstLevelModuleDependencies.each {
            collectVendorAars(it, outFirstLevelAars, outTransitiveAars)
        }
    }

    protected boolean collectVendorAars(ResolvedDependency node,
                                        Set<ResolvedDependency> outFirstLevelAars,
                                        Set<Map> outTransitiveAars) {

        def group = node.group,
            name = node.group,
            version = node.version
        /**
         * 过滤一些模块
         */
        if (group == '' && version == '') {
            // Ignores the dependency of local aar
            return false
        }
//        if (small.splitAars.find { aar -> group == aar.group && name == aar.name } != null) {
//            // Ignores the dependency which has declared in host or lib.*
//            return false
//        }
//        if (small.retainedAars.find { aar -> group == aar.group && name == aar.name } != null) {
//            // Ignores the dependency of normal modules
//            return false
//        }
        String path = "$group/$name/$version"
        def aar = [path: path, group: group, name: node.name, version: version]
        File aarOutput = small.buildCaches.get(path)
        if (aarOutput != null) {
            def resDir = new File(aarOutput, "res")
            // If the dependency has resources, collect it
            if (resDir.exists() && resDir.list().size() > 0) {
                if (outFirstLevelAars != null && !outFirstLevelAars.contains(node)) {
                    outFirstLevelAars.add(node)
                }
                if (!outTransitiveAars.contains(aar)) {
                    outTransitiveAars.add(aar)
                }
                node.children.each { next ->
                    collectVendorAars(next, null, outTransitiveAars)
                }
                return true
            }
        }

        // Otherwise, check it's children for recursively collecting
        boolean flag = false
        node.children.each { next ->
            flag |= collectVendorAars(next, null, outTransitiveAars)
        }
        if (!flag) return false

        if (outFirstLevelAars != null && !outFirstLevelAars.contains(node)) {
            outFirstLevelAars.add(node)
        }
        return true
    }
}