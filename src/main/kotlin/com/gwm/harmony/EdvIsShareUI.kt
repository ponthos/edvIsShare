package com.example.demo

import com.gwm.plugin.data.ui.*
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiDirectory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JComponent
import kotlin.jvm.Throws

class EdvIsShareUI(var e: AnActionEvent, directory: PsiDirectory) : DialogWrapper(false) {
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
                })
            }
        }
    }

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

        refreshProject(e, "")

    }

    /**
     * 刷新项目
     *
     * @param e
     */
    private fun refreshProject(e: AnActionEvent, strAlarm: String) {
        e.project!!.baseDir.refresh(false, true)
        if (getAppPath().contains(""))
            if (strAlarm.isEmpty()) {
                val notifyMessage = "组件化修改成功"
                showNotify(notifyMessage, project)
            } else {
                showNotify(strAlarm, project)
            }
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

    /**
     * 检查原有项目module文件是否符合组件化运行要求，并且进行拷贝到temp。后续还原的时候进行还原处理
     */
    fun checkIsShare() {

    }


    /**
     * 覆盖创建组件化文件
     */
    fun createIsShareFile() {
        createClassFiles(CodeType.Hvigorfile_ts);
        createClassFiles(CodeType.Index_ets);
        createClassFiles(CodeType.Module_json5);
    }

    fun revertIsShareFile() {
        createClassFiles(CodeType.TempHvigorfile_ts)
        createClassFiles(CodeType.TempIndex_ets);
        createClassFiles(CodeType.TempModule_json5);
    }

    private enum class CodeType {
        Hvigorfile_ts, Index_ets, Module_json5, TempHvigorfile_ts, TempIndex_ets, TempModule_json5
    }

    /**
     * 创建对应的shape.xml文件
     */
    private fun createClassFiles(type: CodeType) {
        var fileName = ""
        var content = ""
        when (type) {
            CodeType.Hvigorfile_ts -> {
                fileName = "hvigorfile.ts.ftl"
            }

            CodeType.Index_ets -> {
                fileName = "index.ets.ftl"
            }

            CodeType.Module_json5 -> {
                fileName = "module.json5.ftl"
            }

            CodeType.TempHvigorfile_ts -> fileName = "temphvigorfile.ts.ftl"
            CodeType.TempIndex_ets -> fileName = "tempindex.ets.ftl"
            CodeType.TempModule_json5 -> fileName = "tempmodule.json5.ftl"
        }
        content = ReadTemplateFile(fileName)
        val stringBuilder = StringBuilder()
        content = dealTemplateContent(content)
        stringBuilder.append(Config.lastPath)
//      stringBuilder.append(".xml")
        stringBuilder.append(fileName.replace(".ftl", ""))
        var apppath = getAppPath()
//        if(apppath.contains("src/main/res/drawable")){
        writeToFile(content, getAppPath(), stringBuilder.toString())
        close(1)
//        }else{
//            showNotify("请选择正确的drawable下进行创建", project)
//        }
    }

    /**
     * 生成
     *
     * @param content   类中的内容
     * @param classPath 类文件路径
     * @param className 类文件名称
     */
    private fun writeToFile(content: String, classPath: String, className: String) {
        try {
            val floder = File(classPath)
            if (!floder.exists()) {
                floder.mkdirs()
            }
            val file = File("$classPath/$className")
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
        input = javaClass.getResourceAsStream("/Template/shape_shadow.xml.ftl")
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

//        if (content.contains("\${angle}")) {
//            content = content.replace("\${angle}", angleText.text)
//        }
//        if (content.contains("\${type}")) {
//            content = content.replace("\${type}", type)
//        }
//        if (content.contains("\${startColor}")) {
//            content = content.replace("\${startColor}", startColor.text)
//        }
//        if (content.contains("\${centerColor}")) {
//            content = content.replace("\${centerColor}", centerColor.text)
//        }
//        if (content.contains("\${endColor}")) {
//            content = content.replace("\${endColor}", endColor.text)
//        }
//        if(content.contains("\${topRightRadius}")){
//            content=content.replace("\${topRightRadius}",if(rightTop)angleText.text.toString()+"dp" else "0dp")
//        }
//        if(content.contains("\${topLeftRadius}")){
//            content=content.replace("\${topLeftRadius}", if(leftTop)angleText.text.toString()+"dp" else "0dp")
//        }
//        if(content.contains("\${bottomLeftRadius}")){
//            content=content.replace("\${bottomLeftRadius}", if(leftDown)angleText.text.toString()+"dp" else "0dp")
//        }
//        if(content.contains("\${bottomRightRadius}")){
//            content=content.replace("\${bottomRightRadius}", if(rightDown)angleText.text.toString()+"dp" else "0dp")
//        }
        return content
    }
}