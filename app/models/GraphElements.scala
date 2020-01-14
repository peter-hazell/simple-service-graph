package models
import play.api.libs.json.{Format, Json}

case class GraphElements(nodes: List[GraphNode], edges: List[GraphEdge]) {
  def addNode(graphNode: GraphNode): GraphElements =
    copy(nodes = nodes :+ graphNode)

  def addEdge(graphEdge: GraphEdge): GraphElements =
    copy(edges = edges :+ graphEdge)
}

object GraphElements {
  implicit val format: Format[GraphElements] = Json.format[GraphElements]

  def empty: GraphElements = GraphElements(Nil, Nil)
}
