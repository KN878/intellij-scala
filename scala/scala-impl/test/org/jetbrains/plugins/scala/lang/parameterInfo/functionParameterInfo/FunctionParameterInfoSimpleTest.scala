package org.jetbrains.plugins.scala
package lang
package parameterInfo
package functionParameterInfo


class FunctionParameterInfoSimpleTest_since_2_12 extends FunctionParameterInfoTestBase {

  override def getTestDataPath: String =
    s"${super.getTestDataPath}simple/"

  override protected def supportedIn(version: ScalaVersion): Boolean = version >= Scala_2_12

  def testJavaLibrary() = doTest()
}


class FunctionParameterInfoSimpleTest extends FunctionParameterInfoTestBase {

  override def getTestDataPath: String =
    s"${super.getTestDataPath}simple/"
  
  def testAnnotation() = doTest()

  def testProperty() = doTest()

  def testPropertyInArgumentList() = doTest()

  def testPropertyWithImplicitParameterInArgumentList() = doTest()

  def testPropertyGeneric() = doTest()

  def testPropertyInBlockArg() = doTest()

  def testPropertyCaretAfter() = doTest()

  def testPropertyInInfix() = doTest()

  def testPropertyInInfixTuple() = doTest()

  def testPropertyWithQualifier() = doTest()

  def testDefaultParameter() = doTest()

  def testDefaultParameterFromSources() = doTest()

  def testFromPositionalToNaming() = doTest()

  def testGenericJavaLibrary() = doTest()

  def testImplicitParameter() = doTest()

  def testInfixExpression() = doTest()

  def testInfixTuple() = doTest()

  def testInfixUnit() = doTest()

  def testLocal() = doTest()

  def testNothingExprType() = doTest()

  def testPositionalAfterNamed() = doTest()

  def testScalaLibrary() = doTest()

  def testSimple() = doTest()

  def testSyntheticParameter() = doTest()

  def testTypeRefinement() = doTest()

  def testAliasedMethod() = doTest()

  def testSeveralParameterLists() = doTest()

  def testSeveralParameterLists2() = doTest()

  def testSeveralParameterListsWithImplicit() = doTest()
}