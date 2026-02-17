import type { Edge, Node } from 'reactflow';

type NodeType = 'start' | 'step' | 'decision' | 'end';

interface FlowNodeData {
  type: NodeType;
  title?: string;
  description?: string;
  [key: string]: unknown;
}

export function buildSpecMarkdown(nodes: Node<FlowNodeData>[], edges: Edge[]): string {
  const byId = new Map(nodes.map(n => [n.id, n]));
  const out = new Map<string, Edge[]>();

  for (const e of edges) {
    if (!out.has(e.source)) out.set(e.source, []);
    out.get(e.source)!.push(e);
  }

  const start = nodes.find(n => n.data?.type === 'start') ?? nodes[0];
  const visited = new Set<string>();

  const lines: string[] = [];
  lines.push(`# Fluxo de atendimento (rascunho do cliente)\n`);

  const walk = (id: string, depth: number) => {
    const n = byId.get(id);
    if (!n) return;

    if (visited.has(id)) {
      lines.push(`${'  '.repeat(depth)}- (loop) volta para **${n.data?.title ?? id}**`);
      return;
    }
    visited.add(id);

    const data = n.data ?? {};
    const title = data.title ?? id;
    const type = data.type ?? 'step';
    const desc = (data.description ?? '').toString().trim();

    lines.push(`${'  '.repeat(depth)}- **${title}** (${type})`);
    if (desc) lines.push(`${'  '.repeat(depth)}  - ${desc}`);

    const next = out.get(id) ?? [];
    for (const e of next) {
      const target = byId.get(e.target);
      const targetTitle = (target?.data?.title ?? e.target).toString();
      const label = (e.label ? ` [${e.label}]` : '');
      lines.push(`${'  '.repeat(depth)}  - vai para: **${targetTitle}**${label}`);
      walk(e.target, depth + 1);
    }
  };

  if (start) walk(start.id, 0);
  return lines.join('\n');
}
