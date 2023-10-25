package com.example.demo

import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Component
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * @author: panxuchao
 * @date 2022/09/23
 * @desc 文件选择
 */
class FileChooserResult(private val project: Project) {
    /**
     * 选择生成路径
     * @param title
     * @param toSelect
     * @param roots
     * @return
     */
    fun showFolderSelectionDialog(title: String, toSelect: VirtualFile?, vararg roots: VirtualFile?): VirtualFile? {
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = title
        if (null != roots) {
            descriptor.setRoots(*roots)
        }
        return FileChooser.chooseFile(descriptor, project, toSelect)
    }

    /**
     * 选择xml文件
     * @param parent
     * @return
     */
    fun showFileSelectionDialog(parent: Component?): String {
        // 创建一个默认的文件选取器
        val fileChooser = JFileChooser()

        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(File("."))

        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)
        // 设置是否允许多选
        fileChooser.setMultiSelectionEnabled(true)

        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("xml(*.xml)", "xml"))
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(FileNameExtensionFilter("xml(*.xml)", "xml"))

        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        val result = fileChooser.showOpenDialog(parent)
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            val file = fileChooser.selectedFile

            // 如果允许选择多个文件, 则通过下面方法获取选择的所有文件
            // File[] files = fileChooser.getSelectedFiles();
            return file.absolutePath
        }
        return ""
    }

    /**
     * 选择xml文件
     * @param parent
     * @return
     */
    fun showArkUIFileSelectionDialog(parent: Component?): String {
        // 创建一个默认的文件选取器
        val fileChooser = JFileChooser()

        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(File("."))

        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)
        // 设置是否允许多选
        fileChooser.setMultiSelectionEnabled(true)

        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("ets(*.ets)", "ets"))
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(FileNameExtensionFilter("ets(*.ets)", "ets"))

        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        val result = fileChooser.showOpenDialog(parent)
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            val file = fileChooser.selectedFile

            // 如果允许选择多个文件, 则通过下面方法获取选择的所有文件
            // File[] files = fileChooser.getSelectedFiles();
            return file.absolutePath
        }
        return ""
    }

    /**
     * 选择image文件
     * @param parent
     * @return
     */
    fun showImageSelectionDialog(parent: Component?): String {
        // 创建一个默认的文件选取器
        val fileChooser = JFileChooser()

        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(File("."))

        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)
        // 设置是否允许多选
        fileChooser.setMultiSelectionEnabled(true)

//        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("image(*.jpg, *.png, *.gif)", "jpg", "png", "gif"));
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(FileNameExtensionFilter("image(*.jpg, *.png, *.gif)", "jpg", "png", "gif"))

        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        val result = fileChooser.showOpenDialog(parent)
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            val file = fileChooser.selectedFile

            // 如果允许选择多个文件, 则通过下面方法获取选择的所有文件
            // File[] files = fileChooser.getSelectedFiles();
            return file.absolutePath
        }
        return ""
    }

    companion object {
        fun getInstance(project: Project): FileChooserResult {
            return FileChooserResult(project)
        }
    }
}
