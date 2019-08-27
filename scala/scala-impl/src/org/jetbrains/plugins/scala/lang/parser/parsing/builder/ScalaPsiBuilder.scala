package org.jetbrains.plugins.scala.lang
package parser
package parsing
package builder

import com.intellij.lang.PsiBuilder

/**
  * @author Alexander Podkhalyuzin
  */
trait ScalaPsiBuilder extends PsiBuilder {

  def twoNewlinesBeforeCurrentToken: Boolean

  def newlineBeforeCurrentToken: Boolean

  def disableNewlines(): Unit

  def enableNewlines(): Unit

  def restoreNewlinesState(): Unit

  def isTrailingComma: Boolean

  def isIdBinding: Boolean

  def isMetaEnabled: Boolean

  @inline
  def withEnabledNewlines[T](body: => T): T = {
    enableNewlines()
    try body
    finally restoreNewlinesState()
  }

  @inline
  def withDisabledNewlines[T](body: => T): T = {
    disableNewlines()
    try body
    finally restoreNewlinesState()
  }
}