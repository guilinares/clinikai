import dagre from "dagre";
import type { FlowEdge, FlowNode } from "../domain/flow.types";

export function layoutDagre(nodes: FlowNode[], edges: FlowEdge[]) {
  const g = new dagre.graphlib.Graph();
  g.setDefaultEdgeLabel(() => ({}));
  g.setGraph({ rankdir: "LR", nodesep: 70, ranksep: 120 });

  const W = 280;
  const H = 110;

  nodes.forEach((n) => g.setNode(n.id, { width: W, height: H }));
  edges.forEach((e) => g.setEdge(e.source, e.target));
  dagre.layout(g);

  return nodes.map((n) => {
    const p = g.node(n.id);
    return { ...n, position: { x: p.x - W / 2, y: p.y - H / 2 } };
  });
}
