package org.jetbrains.plugins.scala.testingSupport.scalatest

trait ScalaTest1GoToSourceTest extends ScalaTestGoToSourceTest {
  def getSuccessfulTestPath: List[String] = List("[root]", "Successful test should run fine")
  def getPendingTestPath: List[String] = List("[root]", "pending test should be pending")
  //for ignored test, we launch the whole suite
  def getIgnoredTestPath: List[String] = List("[root]", goToSourceClassName, "pending test should be ignored !!! IGNORED !!!")
  def getFailedTestPath: List[String] = List("[root]", "failed test should fail")

  //TODO: for now, scalaTest v1.x only supports 'topOfClass' location
  def getSuccessfulLocationLine: Int = 2
  def getPendingLocationLine: Int = 2
  def getIgnoredLocationLine: Int = 2
  def getFailedLocationLine: Int = 2
}
