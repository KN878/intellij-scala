package org.jetbrains.jps.incremental.scala.local.worksheet

import java.io.{File, OutputStream}
import java.lang.reflect.InvocationTargetException
import java.net.{URLClassLoader, URLDecoder}

import org.jetbrains.annotations.NotNull
import org.jetbrains.jps.incremental.scala.Client
import org.jetbrains.jps.incremental.scala.data.{CompilerJars, SbtData}
import org.jetbrains.jps.incremental.scala.local.worksheet.compatibility.{JavaClientProvider, JavaILoopWrapperFactory}
import org.jetbrains.jps.incremental.scala.local.worksheet.util.IsolatingClassLoader
import org.jetbrains.jps.incremental.scala.local.{CompilerFactoryImpl, NullLogger}
import org.jetbrains.jps.incremental.scala.remote.Arguments
import sbt.internal.inc.{AnalyzingCompiler, RawCompiler}
import sbt.io.Path
import xsbti.compile.{ClasspathOptionsUtil, ScalaInstance}

class ILoopWrapperFactoryHandler {
  import ILoopWrapperFactoryHandler._

  private var replFactory: (ClassLoader, JavaILoopWrapperFactory, String) = _

  def loadReplWrapperAndRun(commonArguments: Arguments, out: OutputStream,
                            @NotNull client: Client): Unit =  try {
    val compilerJars = commonArguments.compilerData.compilerJars.orNull
    val scalaInstance = CompilerFactoryImpl.createScalaInstance(compilerJars)
    val scalaVersion = findScalaVersionIn(scalaInstance)
    val iLoopFile = getOrCompileReplLoopFile(commonArguments.sbtData, scalaInstance, client)

    replFactory match {
      case (_, _, oldVersion) if oldVersion == scalaVersion =>
      case _ =>
        val loader = createIsolatingClassLoader(compilerJars)
        val iLoopWrapper = new JavaILoopWrapperFactory
        replFactory = (loader, iLoopWrapper, scalaVersion)
    }

    client.progress("Running REPL...")

    val (classLoader, iLoopWrapper, _) = replFactory

    WorksheetServer.patchSystemOut(out)

    val clientProvider: JavaClientProvider = message => client.progress(message)
    iLoopWrapper.loadReplWrapperAndRun(
      scalaToJava(commonArguments.worksheetFiles),
      commonArguments.compilationData.sources.headOption.map(_.getName).getOrElse(""),
      compilerJars.library,
      compilerJars.compiler,
      scalaToJava(compilerJars.extra),
      scalaToJava(commonArguments.compilationData.classpath),
      out,
      iLoopFile,
      clientProvider,
      classLoader
    )
  } catch {
    case e: InvocationTargetException =>
      throw e.getTargetException
  }

  protected def getOrCompileReplLoopFile(sbtData: SbtData, scalaInstance: ScalaInstance, client: Client): File = {
    val home = sbtData.interfacesHome
    val interfaceJar = sbtData.compilerInterfaceJar

    val sourceJar = {
      val f = sbtData.sourceJars._2_11
      new File(f.getParent, "repl-interface-sources.jar")
    }

    val version = findScalaVersionIn(scalaInstance)
    val is213 = version.startsWith("2.13")
    val iLoopWrapperClass = if (is213) "ILoopWrapper213Impl" else "ILoopWrapperImpl"
    val replLabel = s"repl-wrapper-$version-${sbtData.javaClassVersion}-$WRAPPER_VERSION-$iLoopWrapperClass.jar"
    val targetFile = new File(home, replLabel)

    if (!targetFile.exists()) {
      val log = NullLogger
      home.mkdirs()

      findContainingJar(this.getClass) foreach {
        thisJar =>
          client.progress("Compiling REPL runner...")

          val filter = (file: File) => is213 ^ !file.getName.endsWith("213Impl.scala")

          AnalyzingCompiler.compileSources(
            Seq(sourceJar), targetFile, Seq(interfaceJar, thisJar), replLabel,
            new RawCompiler(scalaInstance, ClasspathOptionsUtil.auto(), log) {
              override def apply(sources: Seq[File], classpath: Seq[File], outputDirectory: File, options: Seq[String]): Unit = {
                super.apply(sources.filter(filter), classpath, outputDirectory, options)
              }
            }, log
          )
      }
    }


    targetFile
  }
}

object ILoopWrapperFactoryHandler {
  private val WRAPPER_VERSION = 1

  private def findScalaVersionIn(scalaInstance: ScalaInstance): String =
    CompilerFactoryImpl.readScalaVersionIn(scalaInstance.loader).getOrElse("Undefined")

  private def findContainingJar(clazz: Class[_]): Option[File] = {
    val resource = clazz.getResource(s"/${clazz.getName.replace('.', '/')}.class")

    if (resource == null) return None

    val url = URLDecoder.decode(resource.toString.stripPrefix("jar:file:"), "UTF-8")
    val idx = url.indexOf(".jar!")
    if (idx == -1) return None

    Some(new File(url.substring(0, idx + 4))).filter(_.exists())
  }

  private def createIsolatingClassLoader(compilerJars: CompilerJars): URLClassLoader = {
    val jars = compilerJars.library +: compilerJars.compiler +: compilerJars.extra
    val parent = IsolatingClassLoader.scalaStdLibIsolatingLoader(this.getClass.getClassLoader)
    new URLClassLoader(Path.toURLs(jars), parent)
  }

  //We need this method as scala std lib converts scala collections to its own wrappers with asJava method
  private def scalaToJava[T](seq: Seq[T]): java.util.List[T] = {
    val list = new java.util.ArrayList[T]()
    seq.foreach(list.add)
    list
  }
}