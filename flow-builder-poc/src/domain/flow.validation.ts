import type { FlowEdge, FlowNode, Issue, NodeData, NodeType } from "./flow.types";

function outsOf(nodeId: string, edges: FlowEdge[]) {
  return edges.filter((e) => e.source === nodeId);
}

function insOf(nodeId: string, edges: FlowEdge[]) {
  return edges.filter((e) => e.target === nodeId);
}

function normalizeLabel(s: string) {
  return s.toUpperCase().replace("Ã", "A").replace("Õ", "O");
}

function reachableFrom(startId: string, edges: FlowEdge[]) {
  const adj = new Map<string, string[]>();
  for (const e of edges) {
    if (!adj.has(e.source)) adj.set(e.source, []);
    adj.get(e.source)!.push(e.target);
  }
  const seen = new Set<string>();
  const stack = [startId];
  while (stack.length) {
    const cur = stack.pop()!;
    if (seen.has(cur)) continue;
    seen.add(cur);
    for (const nx of adj.get(cur) ?? []) stack.push(nx);
  }
  return seen;
}

export function validateGraph(nodes: FlowNode[], edges: FlowEdge[]): Issue[] {
  const issues: Issue[] = [];

  const typed = (id: string) =>
    (nodes.find((n) => n.id === id)?.data as NodeData)?.type as NodeType | undefined;

  const starts = nodes.filter((n) => (n.data as NodeData)?.type === "start");
  const ends = nodes.filter((n) => (n.data as NodeData)?.type === "end");

  if (starts.length !== 1) issues.push({ nodeId: starts[0]?.id ?? nodes[0]?.id ?? "start", msg: "Precisa existir exatamente 1 nó de Início." });
  if (ends.length < 1) issues.push({ nodeId: nodes[0]?.id ?? "end", msg: "Precisa existir pelo menos 1 nó de Fim." });

  for (const n of nodes) {
    const type = (n.data as NodeData)?.type as NodeType;
    const outs = outsOf(n.id, edges);
    const ins = insOf(n.id, edges);

    if (type === "start" && ins.length > 0) issues.push({ nodeId: n.id, msg: "Início não deve ter entrada." });
    if (type === "start" && outs.length !== 1) issues.push({ nodeId: n.id, msg: "Início precisa ter exatamente 1 saída." });
    if (type === "step" && outs.length > 1) issues.push({ nodeId: n.id, msg: "Etapa deve ter no máximo 1 saída." });
    if (type === "end" && outs.length !== 0) issues.push({ nodeId: n.id, msg: "Fim não pode ter saída." });

    if (type === "decision") {
      if (outs.length !== 2) issues.push({ nodeId: n.id, msg: "Decisão precisa ter exatamente 2 saídas." });
      else {
        const labels = outs.map((e) => normalizeLabel((e.label ?? "").toString()));
        const hasSim = labels.includes("SIM");
        const hasNao = labels.includes("NAO") || labels.includes("NÃO");
        if (!hasSim || !hasNao) issues.push({ nodeId: n.id, msg: "Decisão precisa ter labels SIM e NÃO." });
      }
    }
  }

  for (const n of nodes) {
    const type = (n.data as NodeData)?.type as NodeType;
    if (type === "start") continue;
    if (insOf(n.id, edges).length === 0) issues.push({ nodeId: n.id, msg: "Nó está solto (sem entrada)." });
  }

  const start = starts[0];
  if (start) {
    const reach = reachableFrom(start.id, edges);
    for (const n of nodes) {
      if (!reach.has(n.id)) issues.push({ nodeId: n.id, msg: "Nó não é alcançável a partir do Início." });
    }
  }

  for (const e of edges) {
    if (typed(e.target) === "start") issues.push({ nodeId: e.target, msg: "Não pode conectar para o Início." });
    if (e.source === e.target) issues.push({ nodeId: e.source, msg: "Não pode conectar um nó nele mesmo." });
  }

  const seen = new Set<string>();
  return issues.filter((i) => {
    const k = `${i.nodeId}::${i.msg}`;
    if (seen.has(k)) return false;
    seen.add(k);
    return true;
  });
}
