package org.jetbrains.sbt
package project

import com.intellij.ide.util.newProjectWizard.AddModuleWizard
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.Step
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.externalSystem.service.project.wizard.SelectExternalProjectStep
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.{ProjectImportBuilder, ProjectOpenProcessor, ProjectOpenProcessorBase}
import javax.swing.Icon

/**
 * @author Pavel Fatin
 */
//class SbtProjectOpenProcessor extends ProjectOpenProcessorBase[SbtProjectImportBuilder] {
//  // Actual detection is done via the canOpenProject method (to "open" projects without build.sbt file).
//  // However, these "extensions" are used inside ProjectOpenProcessorBase.doOpenProject to determine a project root directory.
//  // TODO Don't depend on file extensions in ProjectOpenProcessorBase.doOpenProject to discover a project root (IDEA)
//  override def getSupportedExtensions = Array(Sbt.BuildFile, Sbt.ProjectDirectory)
//
//  override def doGetBuilder(): SbtProjectImportBuilder =
//    ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(classOf[SbtProjectImportBuilder])
//
//  override def canOpenProject(file: VirtualFile): Boolean = SbtProjectImportProvider.canImport(file)
//
//  override def doQuickImport(file: VirtualFile, wizardContext: WizardContext): Boolean = {
//    val path = SbtProjectImportProvider.projectRootPath(file)
//
//    val dialog = new AddModuleWizard(null, path, new SbtProjectImportProvider(getBuilder))
//
//    getBuilder.prepare(wizardContext)
//    getBuilder.getControl(null).setLinkedProjectPath(path)
//
//    dialog.getWizardContext.setProjectBuilder(getBuilder)
//    dialog.navigateToStep((step: Step) => step.isInstanceOf[SelectExternalProjectStep])
//
//    if (StringUtil.isEmpty(wizardContext.getProjectName)) {
//      val projectName = dialog.getWizardContext.getProjectName
//      if (!StringUtil.isEmpty(projectName)) {
//        wizardContext.setProjectName(projectName)
//      }
//    }
//
//    dialog.doFinishAction()
//    true
//  }
//
//  override def getIcon(file: VirtualFile): Icon = {
//    if (file.isDirectory) Sbt.FolderIcon
//    else language.SbtFileType.getIcon
//  }
//
//  // That's a hack to display sbt icon in the open project file chooser (this part of the IDEA API is broken)
//  // TODO Remove this when IDEA API will be properly re-designed
//  override def lookForProjectsInDirectory = false
//}

class SbtProjectOpenProcessor extends ProjectOpenProcessor {

  override def getName: String = Sbt.Name
  override def getIcon: Icon = Sbt.Icon

  override def canOpenProject(file: VirtualFile): Boolean =
    SbtProjectImportProvider.canImport(file)

  override def doOpenProject(virtualFile: VirtualFile, projectToClose: Project, forceOpenInNewFrame: Boolean): Project =
    new SbtOpenProjectProvider().openProject(virtualFile, projectToClose, forceOpenInNewFrame)
}
