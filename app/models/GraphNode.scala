package models
import play.api.libs.json.{Format, Json}

case class GraphNode(data: GraphNodeData)

object GraphNode {
  implicit val format: Format[GraphNode] = Json.format[GraphNode]
}
