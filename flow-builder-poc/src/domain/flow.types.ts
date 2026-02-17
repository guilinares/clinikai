import type { Edge, Node } from "reactflow";

export type NodeType = "start" | "step" | "decision" | "end";

export type NodeData = {
  type: NodeType;
  title: string;
  description: string;
};

export type FlowNode = Node<NodeData>;
export type FlowEdge = Edge;

export type Issue = { nodeId: string; msg: string };

export type FlowSnapshot = {
  nodes: FlowNode[];
  edges: FlowEdge[];
};
