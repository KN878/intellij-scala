package org.jetbrains.jps.incremental.scala.local.worksheet

import java.io.{File, PrintWriter}

import org.jetbrains.jps.incremental.scala.local.worksheet.ILoopWrapper

import scala.reflect.classTag
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.StdReplTags.tagOfIMain
import scala.tools.nsc.interpreter.{ILoop, IMain, NamedParam, Results}

import scala.collection.JavaConverters._

/**
  * User: Dmitry.Naydanov
  * Date: 27.01.17.
  * ATTENTION: when editing ensure to increase the ILoopWrapperFactoryHandler.WRAPPER_VERSION
  */
class ILoopWrapperImpl(out: PrintWriter, projectFullCp: java.util.List[String]) extends ILoop(None, out) with ILoopWrapper {
  override def init(): Unit = {
    val mySettings = new Settings
    mySettings.classpath.value = projectFullCp.asScala.mkString(File.pathSeparator)
    // do not use java class path because it contains scala library jars with version
    // different from one that is used during compilation (it is passed from the plugin classpath)
    mySettings.usejavacp.value = false
    
    this.settings = mySettings
    
    createInterpreter()
    intp.initializeSynchronous()
    intp.quietBind(NamedParam[IMain]("$intp", intp)(tagOfIMain, classTag[IMain]))
    intp.setContextClassLoader()
  }

  override def reset(): Unit = {
    intp.reset()
  }
  
  override def shutdown(): Unit = {
    closeInterpreter()
  }

  override def processChunk(code: String): Boolean = {
    intp.interpret(code) match {
      case Results.Success => true
      case _ => false
    }
  }

  override def getOutputWriter: PrintWriter = out
}
