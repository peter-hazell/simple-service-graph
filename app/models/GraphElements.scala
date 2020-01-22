package models
import play.api.libs.json.{Format, Json}

case class GraphElements(nodes: List[GraphNode], edges: List[GraphEdge]) {
  def addNode(id: String, href: String): GraphElements =
    copy(nodes = nodes :+ GraphNode(data = GraphNodeData(id, href)))

  def addEdge(source: String, target: String): GraphElements =
    copy(edges = edges :+ GraphEdge(data = GraphEdgeData(id = s"$source~$target", source = source, target = target)))
}

object GraphElements {
  implicit val format: Format[GraphElements] = Json.format[GraphElements]

  def empty: GraphElements = GraphElements(Nil, Nil)
}
