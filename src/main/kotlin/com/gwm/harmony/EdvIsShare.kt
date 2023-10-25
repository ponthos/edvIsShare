package com.example.demo

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import java.io.File

class EdvIsShare :AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        if(!checkLastUpdate()){
            showNotify("上次组件化路径尚未还原，请还原后再进行组件化运行",event.getData(PlatformDataKeys.PROJECT))
            return
        }

        val project = event.getData(PlatformDataKeys.PROJECT) ?: return

        val dataContext = event.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return
        val directory = when (val navigatable = LangDataKeys.NAVIGATABLE.getData(dataContext)) {
            is PsiDirectory -> navigatable
            is PsiFile -> navigatable.containingDirectory
            else -> {
                val root = ModuleRootManager.getInstance(module)
                root.sourceRoots
                    .asSequence()
                    .mapNotNull {
                        PsiManager.getInstance(project).findDirectory(it)
                    }.firstOrNull()
            }
        } ?: return

        if(!shareHasExist(directory,event)){
            return
        }

        EdvIsShareUI(event,directory).show()
    }

    /**
     * 判断temp文件是否存有，上次组件化文件，还原并对本次进行修改
     */
    fun checkLastUpdate():Boolean{
        if (Config.lastPath!="")
            return false
        if(Config.lastHvigorFile!="")
            return false
        if(Config.lastModule!="")
            return false
        if(Config.lastIndexPage!="")
            return false
        return true
    }

    /**
     * 判断所需修改文件是否全部存在，除了index会自动创建之外，其他配置需要直接返回错误
     */
    fun shareHasExist(indexPath: PsiDirectory, e: AnActionEvent):Boolean{
        var rootPath = getAppPath(e)
        var psipath=indexPath.toString().replace("PsiDirectory:","")
        rootPath=rootPath.substring(1,rootPath.length)
        var fileHFile = File("$psipath\\hvigorfile.ts")
        var fileModule= File("$psipath\\src\\main\\module.json5")

        if (psipath.contains("src")||psipath.contains("main")){
            showNotify("当前module选择根目录不正确，请重新选择",e.project)
            return false
        }
        if(!fileHFile.exists()){
            showNotify("hvigorfile.ts文件不存在请检查文件相关目录是否正确",e.project)
            return false
        }
        if(!fileModule.exists()){
            showNotify("module.json5文件不存在请检查文件相关目录是否正确",e.project)
            return false
        }
        return true
    }


    /**
     * 获取包名文件路径
     */
    fun getAppPath(e: AnActionEvent): String {
        return LangDataKeys.IDE_VIEW.getData(e.dataContext)?.orChooseDirectory.toString().replace("PsiDirectory", "")
    }
}