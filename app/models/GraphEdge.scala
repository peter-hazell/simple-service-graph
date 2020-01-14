package models
import play.api.libs.json.{Format, Json}

case class GraphEdge(data: GraphEdgeData)

object GraphEdge {
  implicit val format: Format[GraphEdge] = Json.format[GraphEdge]
}







