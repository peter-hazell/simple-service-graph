package models
import play.api.libs.json.{Format, Json}

case class GraphNodeData(id: String)

object GraphNodeData {
  implicit val format: Format[GraphNodeData] = Json.format[GraphNodeData]
}
