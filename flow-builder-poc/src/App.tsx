import { useCallback, useEffect, useMemo, useState } from "react";
import ReactFlow, {
  addEdge,
  Background,
  Controls,
  MiniMap,
  type Connection,
  type NodeTypes,
  type OnConnect,
  type ReactFlowInstance,
  useEdgesState,
  useNodesState,
} from "reactflow";
import "reactflow/dist/style.css";
import "./styles.css";

import StepNode from "./nodes/StepNode";
import { buildSpecMarkdown } from "./flowSpec";

import type {
  FlowEdge,
  FlowNode,
  FlowSnapshot,
  NodeData,
  NodeType,
} from "./domain/flow.types";

import {
  defaultData,
  hasEdge,
  isValidConnection,
  nextDecisionLabel,
  outEdgesOf,
  uid,
} from "./domain/flow.rules";

import { validateGraph } from "./domain/flow.validation";
import { layoutDagre } from "./flow/flow.layout";
import { loadFlow, saveFlow } from "./infra/flow.storage";

import { useUndoRedo } from "./editor/useUndoRedo";
import { useKeyboardShortcuts } from "./editor/useKeyboardShortcuts";
import CustomFlowEdge from "./edges/FlowEdge";

const SESSION_KEY = "clinikai.admin.session";

function getSession(): { clinicId: string; accessToken: string } | null {
  try {
    const raw = localStorage.getItem(SESSION_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    if (parsed?.clinicId && parsed?.accessToken) return parsed;
    return null;
  } catch {
    return null;
  }
}

// Converte o JSON salvo no backend para FlowSnapshot válido.
// Suporta dois formatos:
//   - Novo: { nodes (com position + data), edges } — salvo a partir do snapshot ReactFlow
//   - Antigo: FlowSpecV1 { version, nodes (com title/description direto, sem position), edges }
function restoreSnapshot(
  raw: Record<string, unknown>,
  layout: (nodes: FlowNode[], edges: FlowEdge[]) => FlowNode[],
): FlowSnapshot {
  const rawNodes = (raw.nodes ?? []) as Record<string, unknown>[];
  const rawEdges = (raw.edges ?? []) as Record<string, unknown>[];

  // Formato novo: nodes têm o campo `data`
  const isNewFormat = rawNodes[0]?.data !== undefined;

  if (isNewFormat) {
    const nodes = rawNodes.map((n) => ({
      ...n,
      type: (n.type as string) || "step",
      position: (n.position as { x: number; y: number }) ?? { x: 0, y: 0 },
    })) as FlowNode[];

    const edges = rawEdges.map((e) => ({
      ...e,
      type: (e.type as string) || "flow",
    })) as FlowEdge[];

    return { nodes, edges };
  }

  // Formato antigo (FlowSpecV1): converte para estrutura ReactFlow e aplica layout
  const nodes: FlowNode[] = rawNodes.map((n) => ({
    id: n.id as string,
    type: "step",
    position: { x: 0, y: 0 },
    data: {
      type: n.type as NodeType,
      title: (n.title as string) ?? "",
      description: (n.description as string) ?? "",
    },
  }));

  const edges: FlowEdge[] = rawEdges.map((e) => ({
    id: e.id as string,
    source: e.source as string,
    target: e.target as string,
    type: "flow",
    label: e.label as string | undefined,
  }));

  return { nodes: layout(nodes, edges), edges };
}

export default function App() {
  // ---- tenant ----
  const clinicId = getSession()?.clinicId ?? "local";

  // ---- ReactFlow setup ----
  const nodeTypes: NodeTypes = useMemo(() => ({ step: StepNode }), []);

  const [nodes, setNodes, onNodesChange] = useNodesState<NodeData>([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState<FlowEdge>([]);
  const [rf, setRf] = useState<ReactFlowInstance | null>(null);

  // ---- selection ----
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const selectedNode: FlowNode | null = useMemo(
    () =>
      (selectedId
        ? (nodes.find((n) => n.id === selectedId) as FlowNode)
        : null) ?? null,
    [nodes, selectedId],
  );

  // ---- snapshot + undo/redo ----
  const snapshot: FlowSnapshot = useMemo(
    () => ({ nodes: nodes as FlowNode[], edges: edges as FlowEdge[] }),
    [nodes, edges],
  );

  const applySnapshot = useCallback(
    (s: FlowSnapshot) => {
      setNodes(s.nodes);
      setEdges(s.edges);
    },
    [setNodes, setEdges],
  );

  const { history, future, push, undo, redo } = useUndoRedo(50);

  // ---- validation ----
  const issues = useMemo(
    () => validateGraph(nodes as FlowNode[], edges as FlowEdge[]),
    [nodes, edges],
  );
  const invalidNodeIds = useMemo(
    () => new Set(issues.map((i) => i.nodeId)),
    [issues],
  );

  // ---- initial load: backend first, localStorage fallback ----
  useEffect(() => {
    const session = getSession();

    if (!session) {
      applySnapshot(loadFlow(clinicId));
      return;
    }

    fetch(`/api/clinics/${session.clinicId}/flow`, {
      headers: { Authorization: `Bearer ${session.accessToken}` },
    })
      .then((res) => {
        if (res.status === 204) return null;
        if (!res.ok) return null;
        return res.json();
      })
      .then((raw) => {
        if (raw?.nodes?.length) {
          applySnapshot(restoreSnapshot(raw, layoutDagre));
        } else {
          applySnapshot(loadFlow(session.clinicId));
        }
      })
      .catch(() => applySnapshot(loadFlow(session.clinicId)));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ---- persist ----
  useEffect(() => {
    // evita salvar antes do load preencher
    if (nodes.length === 0 && edges.length === 0) return;
    saveFlow(clinicId, snapshot);
  }, [clinicId, snapshot, nodes.length, edges.length]);

  // ---- keyboard shortcuts ----
  const deleteSelected = useCallback(() => {
    if (!selectedId) return;

    const newNodes = (nodes as FlowNode[]).filter((n) => n.id !== selectedId);
    const newEdges = (edges as FlowEdge[]).filter(
      (e) => e.source !== selectedId && e.target !== selectedId,
    );

    push(snapshot); // salva estado anterior
    setNodes(newNodes);
    setEdges(newEdges);
    setSelectedId(null);
  }, [selectedId, nodes, edges, push, snapshot, setNodes, setEdges]);

  useKeyboardShortcuts({
    onUndo: () => undo(snapshot, applySnapshot),
    onRedo: () => redo(snapshot, applySnapshot),
    onDelete: () => deleteSelected(),
    onEscape: () => setSelectedId(null),
  });

  // ---- connect ----
  const onConnect: OnConnect = useCallback(
    (conn: Connection) => {
      if (!conn.source || !conn.target) return;

      if (!isValidConnection(conn, nodes as FlowNode[])) return;
      if (hasEdge(conn.source, conn.target, edges as FlowEdge[])) return;

      const source = (nodes as FlowNode[]).find((n) => n.id === conn.source);
      const sType = (source?.data as NodeData)?.type as NodeType;

      // limites de saída
      const outs = outEdgesOf(conn.source, edges as FlowEdge[]).length;
      if (sType === "end") return;
      if (sType === "start" && outs >= 1) return;
      if (sType === "step" && outs >= 1) return;

      let label: string | undefined;

      // decision: SIM/NÃO automático e limite 2
      if (sType === "decision") {
        const next = nextDecisionLabel(conn.source, edges as FlowEdge[]);
        if (!next) return;
        label = next;
      }

      // salva snapshot antes da mudança
      push(snapshot);

      setEdges((eds) =>
        addEdge({ ...conn, id: uid("e"), type: "flow", label }, eds),
      );
    },
    [nodes, edges, push, snapshot, setEdges],
  );

  // ---- add node ----
  const addNodeOfType = useCallback(
    (type: NodeType) => {
      const id = uid(type[0]);
      const data = defaultData(type);

      const newNodes: FlowNode[] = [
        ...(nodes as FlowNode[]),
        {
          id,
          type: "step",
          position: { x: 420, y: 280 + nodes.length * 10 },
          data,
        },
      ];

      push(snapshot);
      setNodes(newNodes);

      // opcional: auto-selecionar novo node
      setSelectedId(id);
    },
    [nodes, push, snapshot, setNodes],
  );

  // ---- edit selected ----
  const updateSelected = useCallback(
    (patch: Partial<NodeData>) => {
      if (!selectedId) return;

      // se quiser undo por edição de campo, descomente:
      // push(snapshot);

      setNodes((nds) =>
        (nds as FlowNode[]).map((n) =>
          n.id === selectedId
            ? { ...n, data: { ...(n.data as NodeData), ...patch } }
            : n,
        ),
      );
    },
    [selectedId, setNodes],
  );

  // ---- export ----
  const exportSpec = useCallback(async () => {
    const md = buildSpecMarkdown(nodes as FlowNode[], edges as FlowEdge[]);
    try {
      await navigator.clipboard.writeText(md);
      alert("Roteiro copiado ✅");
    } catch {
      alert(md);
    }
  }, [nodes, edges]);

  // ---- organize ----
  const organize = useCallback(() => {
    push(snapshot);
    setNodes((nds) => layoutDagre(nds as FlowNode[], edges as FlowEdge[]));
  }, [push, snapshot, setNodes, edges]);

  const edgeTypes = useMemo(
    () => ({
      flow: CustomFlowEdge,
    }),
    [],
  );

  const publishFlow = useCallback(async () => {
    if (issues.length > 0) {
      alert("O fluxo possui erros e não pode ser publicado.");
      return;
    }

    const session = getSession();
    if (!session) {
      alert("Sessão não encontrada. Faça login novamente.");
      return;
    }

    try {
      const res = await fetch(`/api/clinics/${session.clinicId}/flow/publish`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${session.accessToken}`,
        },
        body: JSON.stringify({ flow: { nodes, edges } }),
      });

      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }

      alert("Fluxo publicado com sucesso 🚀");
    } catch (err) {
      console.error(err);
      alert("Erro ao publicar fluxo.");
    }
  }, [nodes, edges, issues]);

  return (
    <div className="shell">
      {/* Left panel */}
      <aside className="panel card">
        <div className="status-badge">
          {issues.length === 0 ? "Fluxo válido" : "Fluxo inválido"}
        </div>
        <div className="status-badge invalid">{issues.length} erro(s)</div>

        <h3 className="h1">Blocos</h3>
        <p className="p">
          Monte um pré-fluxo simples. Depois você exporta um roteiro e usa isso
          pra IA refinadora gerar o prompt do atendente.
        </p>

        <div className="stack">
          <button
            className="btn btn-primary"
            onClick={() => addNodeOfType("start")}
            disabled={(nodes as FlowNode[]).some(
              (n) => (n.data as NodeData)?.type === "start",
            )}
          >
            + Início
          </button>

          <button
            className="btn btn-secondary"
            onClick={() => addNodeOfType("step")}
          >
            + Etapa
          </button>

          <button
            className="btn btn-secondary"
            onClick={() => addNodeOfType("decision")}
          >
            + Decisão
          </button>

          <button
            className="btn btn-secondary"
            onClick={() => addNodeOfType("end")}
          >
            + Encerrar
          </button>
        </div>

        <div className="divider"></div>

        <h3 className="h1">Ações</h3>
        <div className="stack">
          <button className="btn btn-secondary" onClick={exportSpec}>
            Copiar roteiro
          </button>

          <button className="btn btn-secondary" onClick={organize}>
            Organizar
          </button>

          <button
            className="btn btn-secondary"
            onClick={() => undo(snapshot, applySnapshot)}
            disabled={history.length === 0}
          >
            Desfazer
          </button>

          <button
            className="btn btn-secondary"
            onClick={() => redo(snapshot, applySnapshot)}
            disabled={future.length === 0}
          >
            Refazer
          </button>

          <button
            className="btn btn-primary"
            disabled={issues.length > 0}
            onClick={publishFlow}
          >
            Publicar
          </button>

          <button
            className="btn btn-danger"
            onClick={deleteSelected}
            disabled={!selectedId}
          >
            Excluir selecionado
          </button>
        </div>

        {issues.length > 0 && (
          <>
            <div className="divider"></div>
            <div className="alert">
              <b>Validação:</b> {issues.length} problema(s).
              {issues.slice(0, 8).map((i, idx) => {
                const title = (
                  (
                    (nodes as FlowNode[]).find((n) => n.id === i.nodeId)
                      ?.data as NodeData
                  )?.title ?? i.nodeId
                ).toString();
                return (
                  <div
                    key={idx}
                    className="item"
                    onClick={() => {
                      setSelectedId(i.nodeId);
                      const n = (nodes as FlowNode[]).find(
                        (n) => n.id === i.nodeId,
                      );
                      if (rf && n) {
                        rf.setCenter(n.position.x, n.position.y, {
                          zoom: 1.1,
                          duration: 250,
                        });
                      }
                    }}
                  >
                    <b>{title}:</b> {i.msg}
                  </div>
                );
              })}
              {issues.length > 8 && (
                <div className="item">…e mais {issues.length - 8}</div>
              )}
            </div>
          </>
        )}

        <div className="divider"></div>
        <span className="tag">POC • LocalStorage</span>
      </aside>

      {/* Canvas */}
      <main className="card canvasCard">
        <div className="canvas">
          <ReactFlow
            onInit={setRf}
            nodes={(nodes as FlowNode[]).map((n) => ({
              ...n,
              className: invalidNodeIds.has(n.id) ? "invalid" : "",
            }))}
            edges={edges}
            edgeTypes={edgeTypes}
            defaultEdgeOptions={{ type: "flow" }}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}
            nodeTypes={nodeTypes}
            fitView
            onNodeClick={(_, n) => setSelectedId(n.id)}
            onPaneClick={() => setSelectedId(null)}
            isValidConnection={(c) => isValidConnection(c, nodes as FlowNode[])}
            snapToGrid
            snapGrid={[16, 16]}
          >
            <Background gap={18} size={1} />
            <MiniMap
              pannable
              zoomable
              nodeColor={(n) => {
                const t = (n.data as NodeData)?.type;
                if (t === "start") return "rgba(41,99,116,.65)";
                if (t === "decision") return "rgba(98,159,173,.70)";
                if (t === "end") return "rgba(12,44,85,.55)";
                return "rgba(12,44,85,.35)";
              }}
            />
            <Controls />
          </ReactFlow>
        </div>
      </main>

      {/* Right panel */}
      <aside className="panel card">
        <h3 className="h1">Editor</h3>

        {!selectedNode ? (
          <p className="p">
            Clique em um bloco para editar título e descrição.
          </p>
        ) : (
          <>
            <div className="tag" style={{ marginBottom: 12 }}>
              {(selectedNode.data as NodeData)?.type?.toUpperCase?.() ?? "STEP"}
            </div>

            <label className="p" style={{ marginBottom: 6 }}>
              Título
            </label>
            <input
              className="input"
              value={(selectedNode.data as NodeData)?.title ?? ""}
              onChange={(e) => updateSelected({ title: e.target.value })}
            />

            <div style={{ height: 10 }} />

            <label className="p" style={{ marginBottom: 6 }}>
              Descrição
            </label>
            <textarea
              className="input"
              rows={7}
              value={(selectedNode.data as NodeData)?.description ?? ""}
              onChange={(e) => updateSelected({ description: e.target.value })}
            />

            <div style={{ height: 12 }} />

            <div className="row">
              <button
                className="btn btn-secondary"
                onClick={() => setSelectedId(null)}
              >
                Fechar
              </button>
            </div>
          </>
        )}
      </aside>
    </div>
  );
}
