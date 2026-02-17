import { Handle, type NodeProps, Position } from "reactflow";

type NodeType = "start" | "step" | "decision" | "end";

const typeLabel = (t: NodeType) => {
  switch (t) {
    case "start":
      return "Início";
    case "step":
      return "Etapa";
    case "decision":
      return "Decisão";
    case "end":
      return "Fim";
  }
};

const iconBg = (t: NodeType) => {
  switch (t) {
    case "start":
      return "rgba(98,159,173,.18)";
    case "decision":
      return "rgba(41,99,116,.14)";
    case "end":
      return "rgba(12,44,85,.10)";
    default:
      return "rgba(237,237,206,.65)";
  }
};

const dotBg = (t: NodeType) => {
  switch (t) {
    case "start":
      return "#296374";
    case "decision":
      return "#296374";
    default:
      return "#0C2C55";
  }
};

export default function StepNode(props: NodeProps) {
  const data = props.data as {
    title: string;
    description: string;
    type: NodeType;
  };

  return (
    <div className={`node t-${data.type}`}>
      {/* Entrada */}
      <Handle className="handle" type="target" position={Position.Left} />

      <div className="nodeTop">
        <div className="nodeIcon" style={{ background: iconBg(data.type) }}>
          <div
            className="nodeIconDot"
            style={{ background: dotBg(data.type) }}
          />
        </div>

        <div className="nodeMeta">
          <div className="node-header">
            <span className={`node-badge t-${data.type}`}>
              {data.type.toUpperCase()}
            </span>
            {/* seu título existente aqui */}
          </div>
          <div className="nodeTitle">{data.title || "Sem título"}</div>
          <div className="nodeType">{typeLabel(data.type)}</div>
        </div>
      </div>

      <div className="nodeBody">{data.description || "—"}</div>

      {/* Saída */}
      {data.type !== "end" && (
        <Handle className="handle" type="source" position={Position.Right} />
      )}
    </div>
  );
}
