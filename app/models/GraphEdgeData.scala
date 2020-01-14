package models
import play.api.libs.json.{Format, Json}

case class GraphEdgeData(id: String, source: String, target: String)

object GraphEdgeData {
  implicit val format: Format[GraphEdgeData] = Json.format[GraphEdgeData]
}
