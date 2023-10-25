package com.gwm.harmony

import com.gwm.plugin.data.ui.*
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JComponent
import kotlin.jvm.Throws

class EdvIsShareUI(var e: AnActionEvent, directory: PsiDirectory) : DialogWrapper(false) {
    var path: String = ""

    var project: Project? = null
    var module: Module? = null


    var currentDate: String? = ""
    var userName: String? = ""
    var computerName: String? = ""
    var userDomain: String? = ""

    init {


        project = e.getData(PlatformDataKeys.PROJECT)
        module = e.dataContext.getData("module") as Module?
        currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())
        val map = System.getenv()
        userName = map["USERNAME"] // 获取用户名

        computerName = map["COMPUTERNAME"] // 获取计算机名

        userDomain = map["USERDOMAIN"] // 获取计算机域名

        init();
        title = "创建"

        e.project!!.baseDir.refresh(false, true)

    }

    override fun createCenterPanel(): JComponent {
        return jGridLayout(2, 1) {
            jLabel("组件化运行")
            jGridLayout(1, 3) {
                jLabel("是否设置默认index,不选择则使用默认")
                var isShareText = jTextInput { }
                jButton("选择index路径", clickListener = {
                    val component = FileChooserResult.getInstance(project!!)
                    val xmlPath =
                        component.showArkUIFileSelectionDialog(this)
                    isShareText.text = xmlPath
                    path = xmlPath
                })
            }
        }
    }

    /**
     * 刷新项目
     *
     * @param e
     */
    private fun refreshProject(e: AnActionEvent, strAlarm: String) {
        e.project!!.baseDir.refresh(false, true)
        showNotify(strAlarm,e.project)
    }

    override fun doOKAction() {
        super.doOKAction()
        createIsShareFile()
        refreshProject(e, "组件化运行修改")
    }

    override fun doCancelAction() {
        super.doCancelAction()
        revertIsShareFile()
        refreshProject(e, "还原动态库")
    }

    /**
     * 获取包名文件路径
     */
    fun getAppPath(): String {
        return LangDataKeys.IDE_VIEW.getData(e.dataContext)?.orChooseDirectory.toString().replace("PsiDirectory", "")
    }

    fun getModuleName(): String {
        val project = e.getData(PlatformDataKeys.PROJECT) ?: return ""

        val dataContext = e.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return ""
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
        } ?: return ""
        var dirs = directory.toString().split("\\")
        return dirs[dirs.size - 1]
    }

    fun getModuleUpName(): String {
        val project = e.getData(PlatformDataKeys.PROJECT) ?: return ""

        val dataContext = e.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return ""
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
        } ?: return ""
        var dirs = directory.toString().split("\\")
        var resultUpName =
            dirs[dirs.size - 1].substring(0, 1).uppercase(Locale.getDefault()) + dirs[dirs.size - 1].substring(1)
        return resultUpName
    }

    /**
     * 检查原有项目module文件是否符合组件化运行要求，并且进行拷贝到temp。后续还原的时候进行还原处理
     */
    fun checkIsShare() {

    }


    /**
     * 覆盖创建组件化文件
     */
    fun createIsShareFile() {
        createClassFiles(CodeType.Hvigorfile_ts)
        createClassFiles(CodeType.Index_ets)
        createClassFiles(CodeType.Module_json5)
        if(path.isNotEmpty())
        createClassFiles(CodeType.main_pages_json)
    }

    fun revertIsShareFile() {
        createClassFiles(CodeType.TempHvigorfile_ts)
        createClassFiles(CodeType.TempIndex_ets)
        createClassFiles(CodeType.TempModule_json5)
        createClassFiles(CodeType.Tempmain_pages_json)
    }

    private enum class CodeType {
        Hvigorfile_ts, Index_ets, Module_json5, main_pages_json, TempHvigorfile_ts, TempIndex_ets, TempModule_json5, Tempmain_pages_json
    }

    /**
     * 创建对应的shape.xml文件
     */
    private fun createClassFiles(type: CodeType) {
        val project = e.getData(PlatformDataKeys.PROJECT) ?: return

        val dataContext = e.dataContext
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

        var fileName = ""
        var content = ""
        var resultPath = ""
        var resultPathNoFile = ""
        when (type) {
            CodeType.Hvigorfile_ts -> {
                fileName = "hvigorfile.ts.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\hvigorfile.ts"
            }

            CodeType.Index_ets -> {
                fileName = "Ability.ts.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\src\\main\\ets\\${getModuleName()}ability\\${getModuleUpName()}Ability.ts"
                resultPathNoFile = "$psipath\\src\\main\\ets\\${getModuleName()}ability"
            }

            CodeType.Module_json5 -> {
                fileName = "module.json5.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\src\\main\\module.json5"
            }

            CodeType.TempHvigorfile_ts -> {
                fileName = "temphvigorfile.ts.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\hvigorfile.ts"
            }

            CodeType.TempIndex_ets -> {
                fileName = "tempAbility.ts.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\src\\main\\ets\\${getModuleName()}ability\\${getModuleUpName()}Ability.ts"
            }

            CodeType.TempModule_json5 -> {
                fileName = "tempmodule.json5.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\src\\main\\module.json5"
            }

            CodeType.main_pages_json -> {
                fileName = "main_pages.json.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\src\\main\\resources\\base\\profile\\main_pages.json"
            }

            CodeType.Tempmain_pages_json -> {
                fileName = "tempmain_pages.json.ftl"
                var psipath = directory.toString().replace("PsiDirectory:", "")
                resultPath = "$psipath\\src\\main\\resources\\base\\profile\\main_pages.json"
            }
        }
        content = ReadTemplateFile(fileName)
        val stringBuilder = StringBuilder()
        content = dealTemplateContent(content)
        stringBuilder.append(fileName.replace(".ftl", ""))
        if (resultPathNoFile.isNotEmpty()) {
            writeToFile(content, resultPathNoFile, "${getModuleUpName()}Ability.ts")
        } else {
            writeToFile(content, resultPath)
        }
        close(1)
    }

    /**
     * 生成
     *
     * @param content   类中的内容
     * @param classPath 类文件路径
     * @param className 类文件名称
     */
    private fun writeToFile(content: String, classPath: String) {
        try {
            val floder = File(classPath)
            if (!floder.exists()) {
                floder.mkdirs()
            }
            val file = File(classPath)
            if (!file.exists()) {
                file.createNewFile()
            }
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 生成
     *
     * @param content   类中的内容
     * @param classPath 类文件路径
     * @param className 类文件名称
     */
    private fun writeToFile(content: String, resultPath: String, className: String) {
        try {
            val flder = File(resultPath)
            if (!flder.exists()) {
                flder.mkdirs()
            }

            val file = File(resultPath + "\\" + className)
            if (!file.exists()) {
                file.createNewFile()
            }
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 读取模板文件中的字符内容
     *
     * @param fileName 模板文件名
     * @return
     */
    private fun ReadTemplateFile(fileName: String): String {
        var input: InputStream? = null
        this.getAppPath()
        input = javaClass.getResourceAsStream("/IsShare/" + fileName)
        var content = ""
        try {
            content = String(readStream(input)!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content
    }


    @Throws(IOException::class)
    private fun readStream(inputStream: InputStream?): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        var len = -1
        val buffer = ByteArray(1024)
        try {
            while (inputStream!!.read(buffer).also { len = it } != -1) {
                outputStream.write(buffer, 0, len)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream.close()
            inputStream!!.close()
        }
        return outputStream.toByteArray()
    }


    /**
     * 替换模板中字符
     *
     * @param content
     * @return
     */
    private fun dealTemplateContent(content: String): String {
        var content = content

        if (content.contains("\${module_name}")) {
            content = content.replace("\${module_name}", getModuleName())
        }
        if (content.contains("\${moduleUpName}")) {
            content = content.replace("\${moduleUpName}", getModuleUpName())
        }
        if (content.contains("\${moduleUpName}")) {
            content = content.replace("\${moduleUpName}", getModuleUpName())
        }
        if (content.contains("\${indexPage}")) {
            if (path.isNotEmpty()) {
                var path = path.split("\\")
                content = content.replace("\${indexPage}", path[path.size - 1].substring(0,path[path.size - 1].length-4))
            }
        }
        return content
    }
}