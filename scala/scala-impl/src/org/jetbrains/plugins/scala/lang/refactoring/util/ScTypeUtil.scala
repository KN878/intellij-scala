package org.jetbrains.plugins.scala
package lang
package refactoring
package util

import org.jetbrains.plugins.scala.lang.psi.types.ScType
import org.jetbrains.plugins.scala.lang.psi.types.api.ParameterizedType


/**
 * User: Alexander Podkhalyuzin
 * Date: 30.03.2009
 */

object ScTypeUtil {

  def stripTypeArgs(tp: ScType): ScType = tp match {
    case ParameterizedType(designator, _) => designator
    case t => t
  }
}
