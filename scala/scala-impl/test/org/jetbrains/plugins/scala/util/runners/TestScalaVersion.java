package org.jetbrains.plugins.scala.util.runners;

import org.jetbrains.plugins.scala.*;

// required at compile time to use in annotations
public enum TestScalaVersion {

    Scala_2_10_0, Scala_2_10,
    Scala_2_11_0, Scala_2_11,
    Scala_2_12_0, Scala_2_12,
    Scala_2_13_0, Scala_2_13,
    Scala_3_0
    ;

    public org.jetbrains.plugins.scala.ScalaVersion toProductionVersion() {
        switch (this) {
            case Scala_2_10: return Scala_2_10$.MODULE$;
            case Scala_2_11: return Scala_2_11$.MODULE$;
            case Scala_2_12: return Scala_2_12$.MODULE$;
            case Scala_2_13: return Scala_2_13$.MODULE$;
            case Scala_2_10_0: return Scala_2_10$.MODULE$.withMinor(0);
            case Scala_2_11_0: return Scala_2_11$.MODULE$.withMinor(0);
            case Scala_2_12_0: return Scala_2_12$.MODULE$.withMinor(0);
            case Scala_2_13_0: return Scala_2_13$.MODULE$.withMinor(0);
            case Scala_3_0: return Scala_3_0$.MODULE$;
            default: return null; // unreachable code
        }
    };
}
