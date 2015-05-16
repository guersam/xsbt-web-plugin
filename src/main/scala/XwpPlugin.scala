package com.earldouglas.xwp

import sbt._
import Keys._


object JettyPlugin extends AutoPlugin {

  object autoImport {
    lazy val Jetty = config("jetty").hide
  }

  import autoImport._
  import ContainerPlugin.autoImport._

  override def requires = ContainerPlugin

  override val projectConfigurations = Seq(Jetty)

  override lazy val projectSettings =
    ContainerPlugin.containerSettings(Jetty) ++
      inConfig(Jetty)(jettySettings)

  lazy val jettySettings = Seq(
    containerLibs      := Seq(("org.eclipse.jetty" % "jetty-runner" % "9.2.1.v20140609").intransitive())
  , containerMain      := "org.eclipse.jetty.runner.Runner"
  , containerLaunchCmd := jettyLaunchCmd.value
  )

  lazy val jettyLaunchCmd = Def.setting {
    val portArg: Seq[String] = containerPort.value match {
      case p if p > 0 => Seq("--port", p.toString)
      case _ => Nil
    }

    val configArg: Seq[String] = containerConfigFile.value match {
      case Some(file) => Seq("--config", file.absolutePath)
      case None => Nil
    }

    Seq(containerMain.value) ++
      portArg ++
      configArg ++
      containerArgs.value :+
      (target in WebappPlugin.autoImport.webappPrepare).value.absolutePath
  }
}

object TomcatPlugin extends AutoPlugin {

  object autoImport {
    lazy val Tomcat = config("tomcat").hide
  }

  import autoImport._
  import ContainerPlugin.autoImport._

  override def requires = ContainerPlugin

  override val projectConfigurations = Seq(Tomcat)

  override lazy val projectSettings =
    ContainerPlugin.containerSettings(Tomcat) ++
    inConfig(Tomcat)(tomcatSettings)

  lazy val tomcatSettings = Seq(
    containerLibs      := Seq(("com.github.jsimone" % "webapp-runner" % "7.0.34.1").intransitive())
  , containerMain      := "webapp.runner.launch.Main"
  , containerLaunchCmd := tomcatLaunchCmd.value
  )

  lazy val tomcatLaunchCmd = Def.setting {
    val portArg: Seq[String] = containerPort.value match {
      case p if p > 0 => Seq("--port", p.toString)
      case _ => Nil
    }

    val configArg: Seq[String] = containerConfigFile.value match {
      case Some(file) => Seq("--config", file.absolutePath)
      case None => Nil
    }

    Seq(containerMain.value) ++
      portArg ++
      configArg ++
      containerArgs.value :+
      (target in WebappPlugin.autoImport.webappPrepare).value.absolutePath
  }
}