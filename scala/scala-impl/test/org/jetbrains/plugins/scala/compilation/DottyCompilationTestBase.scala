package org.jetbrains.plugins.scala.compilation

import java.net.{URL, URLClassLoader}

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.testFramework.CompilerTester
import org.jetbrains.plugins.scala.{ScalaVersion, Scala_3_0, ScalacTests}
import org.jetbrains.plugins.scala.extensions.inWriteAction
import org.jetbrains.plugins.scala.base.ScalaSdkOwner
import org.jetbrains.plugins.scala.base.libraryLoaders.LibraryLoader
import org.jetbrains.plugins.scala.debugger.ScalaCompilerTestBase.ListCompilerMessageExt
import org.jetbrains.plugins.scala.debugger.ScalaCompilerTestBase
import org.jetbrains.plugins.scala.performance.DownloadingAndImportingTestCase
import org.jetbrains.plugins.scala.project.settings.ScalaCompilerConfiguration
import org.jetbrains.plugins.scala.project.{IncrementalityType, LibraryExt, ModuleExt}
import org.junit.experimental.categories.Category

/**
 * This test:
 * - downloads dotty project from GitHub
 * - then compiles it
 * - and then loads Main class
 */
@Category(Array(classOf[ScalacTests]))
abstract class DottyCompilationTestBase(incrementalityType: IncrementalityType,
                                        useCompileServer: Boolean = false)
  extends DownloadingAndImportingTestCase
    with ScalaSdkOwner {

  override protected def supportedIn(version: ScalaVersion): Boolean = version == Scala_3_0

  override def githubUsername: String = "lampepfl"

  override def githubRepoName: String = "dotty-example-project"

  override def revision: String = "master"

  override protected def librariesLoaders: Seq[LibraryLoader] = Seq.empty

  private var compiler: CompilerTester = _

  override def setUp(): Unit = {
    super.setUp()

    CompilerTestUtil.enableCompileServer(useCompileServer)
    ScalaCompilerConfiguration.instanceIn(myProject).incrementalityType = incrementalityType
    compiler = new CompilerTester(module)
  }

  override def tearDown(): Unit = try {
    compiler.tearDown()
    ScalaCompilerTestBase.stopAndWait()
    val table = ProjectJdkTable.getInstance
    inWriteAction {
      table.getAllJdks.foreach(table.removeJdk)
    }
  } finally {
    compiler = null
    super.tearDown()
  }

  def testDownloadCompileLoadClass(): Unit = {
    compiler.rebuild().assertNoProblems()

    val urls = (librariesUrls + targetUrl).toArray
    val classLoader = new URLClassLoader(urls, null)

    classLoader.loadClass("Main")
  }

  private def librariesUrls: Set[URL] =
    module.libraries.flatMap(_.jarUrls).toSet

  private def targetUrl: URL =
    new URL(CompilerModuleExtension.getInstance(module).getCompilerOutputUrl + "/")

  private def module: Module =
    getModule(githubRepoName)
}

class DottyIdeaCompilationTest
  extends DottyCompilationTestBase(IncrementalityType.IDEA)

class DottySbtCompilationTest
  extends DottyCompilationTestBase(IncrementalityType.SBT)
