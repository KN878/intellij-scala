package org.jetbrains.plugins.scala.project.settings

import java.util.concurrent.atomic.AtomicReference

/**
 * @author Pavel Fatin
 */
// TODO This class is needed for the "imported" ScalaCompilerConfigurationPanel.
// TODO It's better to replace it with immutable case classes later.
class ScalaCompilerSettingsProfile(name: String) {

  private var myName: String = name
  private val myModuleNames: AtomicReference[List[String]] = new AtomicReference(Nil)
  private var mySettings = ScalaCompilerSettings.fromState(new ScalaCompilerSettingsState)

  def getName: String = myName

  def initFrom(profile: ScalaCompilerSettingsProfile): Unit = {
    ScalaCompilerConfiguration.incModificationCount()
    copyFrom(profile)
  }

  def copy: ScalaCompilerSettingsProfile = {
    val profile = new ScalaCompilerSettingsProfile(name)
    profile.copyFrom(this)
    profile
  }

  private def copyFrom(profile: ScalaCompilerSettingsProfile): Unit = {
    myName = profile.getName
    mySettings = profile.getSettings
    myModuleNames.set(profile.moduleNames)
  }

  def moduleNames: List[String] = myModuleNames.get()

  def addModuleName(name: String): Unit = {
    ScalaCompilerConfiguration.incModificationCount()
    myModuleNames.getAndUpdate(list => name :: list)
  }

  def removeModuleName(name: String): Unit = {
    ScalaCompilerConfiguration.incModificationCount()
    myModuleNames.getAndUpdate(list => list.filter(_ != name))
  }

  def getSettings: ScalaCompilerSettings = mySettings

  def setSettings(settings: ScalaCompilerSettings): Unit = {
    ScalaCompilerConfiguration.incModificationCount()
    mySettings = settings
  }

  override def toString: String = myName
}
