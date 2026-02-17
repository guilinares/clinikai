import type { FlowSnapshot } from "../domain/flow.types";

export const INITIAL_TEMPLATE: FlowSnapshot = {
  nodes: [
    {
      id: "start",
      type: "step",
      position: { x: 120, y: 160 },
      data: { type: "start", title: "Início", description: "Mensagem chegou no WhatsApp" },
    },
    {
      id: "s1",
      type: "step",
      position: { x: 520, y: 160 },
      data: { type: "step", title: "Acolher", description: "Cumprimentar, perguntar nome e o motivo do contato" },
    },
    {
      id: "d1",
      type: "step",
      position: { x: 920, y: 160 },
      data: { type: "decision", title: "É urgência?", description: "Dor forte, sangramento, febre? (SIM/NÃO)" },
    },
    {
      id: "s2",
      type: "step",
      position: { x: 1320, y: 90 },
      data: { type: "step", title: "Agendar", description: "Oferecer horários e confirmar" },
    },
    {
      id: "end",
      type: "step",
      position: { x: 1320, y: 250 },
      data: { type: "end", title: "Encerrar", description: "Confirmar informações e se despedir" },
    },
  ],
  edges: [
    { id: "e1", source: "start", target: "s1" },
    { id: "e2", source: "s1", target: "d1" },
    { id: "e3", source: "d1", target: "s2", label: "NÃO" },
    { id: "e4", source: "d1", target: "end", label: "SIM" },
  ],
};
