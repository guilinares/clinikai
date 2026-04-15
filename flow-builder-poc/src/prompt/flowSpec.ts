import type { FlowEdge, FlowNode, NodeData, NodeType } from "../domain/flow.types";

export type FlowSpecV1 = {
  version: 1;
  clinicId: string;
  startNodeId: string;
  nodes: Array<{
    id: string;
    type: NodeType;
    title: string;
    description: string;
  }>;
  edges: Array<{
    id: string;
    source: string;
    target: string;
    label?: string;
  }>;
};

function normalizeLabel(s?: string) {
  return (s ?? "").trim().toUpperCase();
}

export function buildFlowSpec(clinicId: string, nodes: FlowNode[], edges: FlowEdge[]): FlowSpecV1 {
  const start = nodes.find((n) => (n.data as NodeData)?.type === "start") ?? nodes[0];

  return {
    version: 1,
    clinicId,
    startNodeId: start?.id ?? "start",
    nodes: nodes.map((n) => ({
      id: n.id,
      type: (n.data as NodeData).type,
      title: ((n.data as NodeData).title ?? "").trim(),
      description: ((n.data as NodeData).description ?? "").trim(),
    })),
    edges: edges.map((e) => ({
      id: e.id,
      source: e.source,
      target: e.target,
      label: normalizeLabel(e.label?.toString()),
    })),
  };
}
