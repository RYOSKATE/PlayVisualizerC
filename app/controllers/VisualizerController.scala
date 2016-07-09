package controllers

import javax.inject._

import com.fasterxml.jackson.databind.ObjectMapper
import net.arnx.jsonic.JSON

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection._
import models._
//import net.unicoen._
//import net.unicoen.node._
import net.unicoen.mapper.CPP14Mapper
import net.unicoen.interpreter._
import net.unicoen.node._
import java.io.ByteArrayOutputStream
import java.io.PrintStream

import scala.collection.mutable.ArrayBuffer
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class VisualizerController @Inject() extends Controller {

  //@TODO
  // ### 9999番ポートでデバックする
  // $ activator -jvm-debug 9999 run

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  //index.scala.htmlがview
  def index = Action {
    Ok(views.html.visualizer("This is Visualizer Page.",""))
  }

  var count = 0
  var engine : Engine = new Engine()
  var textOnEditor = ""
  var stackStateString = ""


  val form = Form( "name" -> text )

  def rawDataToUniTree(string:String)={
    new CPP14Mapper(true).parse(string)
  }

  def compile = Action { implicit request =>
    val text = form.bindFromRequest.get
    val rawData = text//.replaceAll("(\r\n|\r|\n)"," ");
  val treeData = rawDataToUniTree(rawData)
    val jsonData = net.arnx.jsonic.JSON.encode(treeData)
    Ok(views.html.visualizer(jsonData,"compile"))
  }

  def startStepExec = Action { implicit request =>
    count = 0
    val text = form.bindFromRequest.get
    textOnEditor = text
    engine = new Engine()
    val baos = new ByteArrayOutputStream()
    engine.out = new PrintStream(baos)
    val node = rawDataToUniTree(text)
    val state = engine.startStepExecution(node.asInstanceOf[UniMethodDec])
    val jsonData = net.arnx.jsonic.JSON.encode(state)
    stackStateString = jsonData
    Ok(views.html.visualizer(jsonData,"debug"))
  }

  def execAll = Action { implicit request =>
    var state : ExecState = null
    do{
      count += 1;
      state = engine.stepExecute()
    }while (engine.getStepExecing())

    val jsonData = net.arnx.jsonic.JSON.encode(state)
    stackStateString = jsonData
    Ok(views.html.visualizer(stackStateString, "EOF"))
  }

  def execOneStep = Action { implicit request =>
    count += 1
    if(engine.getStepExecing()) {
      val state = engine.stepExecute()
      val jsonData = net.arnx.jsonic.JSON.encode(state)
      stackStateString = jsonData
      Ok(views.html.visualizer(jsonData,"nextStep"))
    }
    else{
      Ok(views.html.visualizer(stackStateString, "EOF"))
    }
  }

  def stopDebug = Action { implicit request =>
    engine = null
    Ok(views.html.visualizer(stackStateString, "STOP"))
  }

}