package org.jetbrains.plugins.scala
package lang
package parser
package parsing
package top

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder
import org.jetbrains.plugins.scala.lang.parser.parsing.params.{ClassConstr, TypeParamClause}

sealed abstract class TemplateDef extends ParsingRule {

  protected def parseConstructor()(implicit builder: ScalaPsiBuilder): Unit = {}

  protected def extendsBlockRule: Template

  override final def apply()(implicit builder: ScalaPsiBuilder): Boolean =
    builder.getTokenType match {
      case ScalaTokenTypes.tIDENTIFIER =>
        builder.advanceLexer() // Ate identifier

        parseConstructor()
        extendsBlockRule()

        true
      case _ =>
        builder.error(ScalaBundle.message("identifier.expected"))
        false
    }
}

/**
 * * [[ClassDef]] ::= id [[ClassConstr]] [ [[ClassTemplate]] ]
 */
object ClassDef extends TemplateDef {

  override protected def parseConstructor()(implicit builder: ScalaPsiBuilder): Unit =
    ClassConstr()

  override protected def extendsBlockRule: ClassTemplate.type = ClassTemplate
}

/**
 * [[TraitDef]] ::= id [ [[TypeParamClause]] ] [ [[TraitTemplate]] ]
 */
object TraitDef extends TemplateDef {

  override protected def parseConstructor()(implicit builder: ScalaPsiBuilder): Unit =
    ClassConstr()

  override protected def extendsBlockRule: TraitTemplate.type = TraitTemplate
}

/**
 * [[ObjectDef]] ::= id [ [[ClassTemplate]] ]
 */
object ObjectDef extends TemplateDef {

  override protected def extendsBlockRule: ClassTemplate.type = ClassTemplate
}

/**
 * [[EnumDef]] ::= id [[ClassConstr]] [[EnumTemplate]]
 */
object EnumDef extends TemplateDef {

  override protected def parseConstructor()(implicit builder: ScalaPsiBuilder): Unit =
    ClassConstr()

  override protected def extendsBlockRule: EnumTemplate.type = EnumTemplate
}
