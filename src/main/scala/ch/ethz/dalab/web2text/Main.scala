package ch.ethz.dalab.web2text

import java.io.{BufferedWriter, File, FileWriter}

import ch.ethz.dalab.web2text.cdom.CDOM
import ch.ethz.dalab.web2text.cleaneval.CleanEval
import ch.ethz.dalab.web2text.features.FeatureExtractor
import ch.ethz.dalab.web2text.features.extractor._
import ch.ethz.dalab.web2text.output.CsvDatasetWriter
import ch.ethz.dalab.web2text.utilities.Util
import spray.json._

import scala.io.Source
//import DefaultJsonProtocol._

// https://github.com/spray/spray-json#providing-jsonformats-for-case-classes
case class MyOutput(blockFeatures: Array[Double], edgeFeatures: Array[Double], texts: Array[String], tags: Array[String])

object MyOutput

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val myOutputFormat = jsonFormat4(MyOutput.apply)
}

import ch.ethz.dalab.web2text.MyJsonProtocol._


object Main {

  def main(args: Array[String]): Unit = {
    val text = getFromStdin()
    //    val text = getFromFile("/Users/onurkuru/work/web2textFEATURES/data/bbc-news-page.html")

    val jsonOutput = getJsonOutput(text)
    //    writeToFile("data/json_test.json", jsonOutput)
    println(jsonOutput)
  }

  def getFromStdin(): String = {
    val lines = Source.stdin.getLines()
    val text = lines.mkString("\n")
    return text
  }

  def getFromFile(html_file_path: String): String = {
    val bufferedSource = Source.fromFile(html_file_path)
    val fileContents = bufferedSource.getLines.mkString
    bufferedSource.close
    return fileContents
  }

  def writeToFile(filePath: String, text: String) = {
    val file = new File(filePath)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(text)
    bw.close()
  }

  def getJsonOutput(html_text: String): String = {
    val dom = CDOM.fromHTML(html_text)
    val fe = FeatureExtractor(
      DuplicateCountsExtractor
        + LeafBlockExtractor
        + AncestorExtractor(NodeBlockExtractor + TagExtractor(mode = "node"), 1)
        + AncestorExtractor(NodeBlockExtractor, 2)
        + RootExtractor(NodeBlockExtractor)
        + TagExtractor(mode = "leaf"),
      TreeDistanceExtractor + BlockBreakExtractor + CommonAncestorExtractor(NodeBlockExtractor)
    )
    val leaf_texts = new Array[String](dom.leaves.length)
    val leaf_types = new Array[String](dom.leaves.length)
    for (i <- 0 to (leaf_texts.length - 1)) {
      leaf_texts(i) = dom.leaves(i).text
      leaf_types(i) = dom.leaves(i).tags.mkString(" ")
    }
    val features = fe(dom)
    val myOutput = new MyOutput(features.blockFeatures.data, features.edgeFeatures.data, leaf_texts, leaf_types)
    return myOutput.toJson.toString()
  }

  def exportFeaturesTest = {
    val fe = FeatureExtractor(
      DuplicateCountsExtractor
        + LeafBlockExtractor
        + AncestorExtractor(NodeBlockExtractor + TagExtractor(mode = "node"), 1)
        + AncestorExtractor(NodeBlockExtractor, 2)
        + RootExtractor(NodeBlockExtractor)
        + TagExtractor(mode = "leaf"),
      TreeDistanceExtractor + BlockBreakExtractor + CommonAncestorExtractor(NodeBlockExtractor)
    )
    val data = Util.time {
      CleanEval.dataset(fe)
    }
    //    CsvDatasetWriter.write(data, "/Users/thijs/Desktop/export")
    CsvDatasetWriter.write(data, "/Users/onurkuru/work/web2textFEATURES/export")
    println("# Block features")
    fe.blockExtractor.labels.foreach(println)
    println("# Edge features")
    fe.edgeExtractor.labels.foreach(println)
  }

  def testCommonAncestorExtractor = {
    val ex = CommonAncestorExtractor(LeafBlockExtractor)
    val cdom = CDOM.fromHTML("<body><h1>Header</h1><p>Paragraph with an <i>Italic</i> section.</p></body>");
    ex(cdom)(cdom.leaves(2), cdom.leaves(1))
  }

  def evaluateOtherMethods = {
    val dir = "other_frameworks/output/"
    val cleaners = Iterable(
      "victor" -> ((id: Int) => s"$dir/victor/$id-aligned.txt"),
      "bte" -> ((id: Int) => s"$dir/bte/$id-aligned.txt"),
      "article-extractor" -> ((id: Int) => s"$dir/article-extractor/$id-aligned.txt"),
      "default-extractor" -> ((id: Int) => s"$dir/default-extractor/$id-aligned.txt"),
      "largest-content" -> ((id: Int) => s"$dir/largestcontent-extractor/$id-aligned.txt"),
      "unfluff" -> ((id: Int) => s"$dir/unfluff/$id-aligned.txt")
    )

    for ((label, filenameGen) <- cleaners) {
      val title = s"#### Evaluating ‘${label.capitalize}’ "
      println(s"\n$title${"#" * (82 - title.length)}\n")
      Util.time {
        val eval = CleanEval.evaluateCleaner(filenameGen)
        println(s"$eval")
      }
    }
  }

  def alignCleanEvalData = {
    val projectPath = "/Users/onurkuru/work/web2textFEATURES"
    val dir = s"$projectPath/src/main/resources/cleaneval/aligned"
    CleanEval.generateAlignedFiles(dir)
  }
}
