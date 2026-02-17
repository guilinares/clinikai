import type { Connection } from "reactflow";
import type { FlowEdge, FlowNode, NodeData, NodeType } from "./flow.types";

export function uid(prefix: string) {
  return `${prefix}_${Math.random().toString(16).slice(2, 8)}${Date.now().toString(16).slice(-4)}`;
}

export function defaultData(type: NodeType): NodeData {
  switch (type) {
    case "start": return { type, title: "Início", description: "Mensagem chegou no WhatsApp" };
    case "decision": return { type, title: "Decisão", description: "Defina a regra: se SIM vai para X, se NÃO vai para Y" };
    case "end": return { type, title: "Encerrar", description: "Finalizar atendimento / confirmar / despedir" };
    default: return { type, title: "Etapa", description: "Descreva o que deve acontecer nesta etapa" };
  }
}

export function outEdgesOf(nodeId: string, edges: FlowEdge[]) {
  return edges.filter((e) => e.source === nodeId);
}

export function hasEdge(source: string, target: string, edges: FlowEdge[]) {
  return edges.some((e) => e.source === source && e.target === target);
}

export function nextDecisionLabel(sourceId: string, edges: FlowEdge[]) {
  const labels = outEdgesOf(sourceId, edges).map((e) => (e.label ?? "").toString().toUpperCase());
  if (!labels.includes("SIM")) return "SIM";
  if (!labels.includes("NÃO") && !labels.includes("NAO")) return "NÃO";
  return null;
}

export function isValidConnection(conn: Connection, nodes: FlowNode[]) {
  if (!conn.source || !conn.target) return false;
  if (conn.source === conn.target) return false;

  const source = nodes.find((n) => n.id === conn.source);
  const target = nodes.find((n) => n.id === conn.target);
  if (!source || !target) return false;

  const sType = (source.data as NodeData)?.type as NodeType;
  const tType = (target.data as NodeData)?.type as NodeType;

  if (sType === "end") return false;
  if (tType === "start") return false;
  return true;
}
