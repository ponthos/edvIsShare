package com.gwm.harmony

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
//import com.gwm.plugin.tool.model.ConfigManager
import com.intellij.notification.*
import java.lang.IllegalStateException
import java.util.regex.Pattern

/**
 * File contains functions which simply other functions's invoke
 * Created by pxnxuchao
 */


/**
 * do the action that could be roll-back
 */
fun executeCouldRollBackAction(project: Project?, action: (Project?) -> Unit) {
    CommandProcessor.getInstance().executeCommand(project, {
        ApplicationManager.getApplication().runWriteAction {
            action.invoke(project)
        }
    }, "insertKotlin", "JsonToKotlin")
}

/**
 * get class string block list from a big string which contains classes
 */
fun getClassesStringList(classesString: String): List<String> {
    return classesString.split("\n\n").filter { it.isNotBlank() }
}

/**
 * export the class name from class block string
 */
fun getClassNameFromClassBlockString(classBlockString: String): String {
    val pattern = Pattern.compile("(data )?class (?<className>[^(]+).*")
    val matcher = pattern.matcher(classBlockString)
    if (matcher.find()) {
        return matcher.group("className").trim()
    }
    throw IllegalStateException("cannot find class name in classBlockString: $classBlockString")
}

fun replaceClassNameToClassBlockString(classBlockString: String, newClassName: String): String {
    val blockPre = classBlockString.substringBefore("data class")
    val blockAfter = classBlockString.substringAfter("(")
    val blockMid = "data class $newClassName("
    return blockPre + blockMid + blockAfter
}

fun showNotify(notifyMessage: String, project: Project?) {
//    val notificationGroup = NotificationGroup("JSON to Kotlin Class", NotificationDisplayType.BALLOON, true)
    val  groupManager = NotificationGroupManager.getInstance()
    ApplicationManager.getApplication().invokeLater {
        val notification = groupManager.getNotificationGroup("Custom Notification Group").createNotification(notifyMessage, NotificationType.INFORMATION)
        Notifications.Bus.notify(notification, project)
    }
//    NotificationGroupManager.getInstance()
//        .getNotificationGroup("Custom Notification Group")
//        .createNotification(notifyMessage, NotificationType.INFORMATION)
//        .notify(project);
}

fun <E> List<E>.firstIndexAfterSpecificIndex(element: E, afterIndex: Int): Int {
    forEachIndexed { index, e ->
        if (e == element && index > afterIndex) {
            return index
        }
    }
    return -1
}

fun getCommentCode(comment: String): String {
    return comment.replace(Regex("[\n\r]"), "")
}
