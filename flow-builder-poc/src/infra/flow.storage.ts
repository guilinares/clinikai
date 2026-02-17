import type { FlowSnapshot } from "../domain/flow.types";
import { INITIAL_TEMPLATE } from "../flow/flow.template";

export function flowStorageKey(clinicId: string) {
  return `clinikai_flow_${clinicId}_v1`;
}

export function loadFlow(clinicId: string): FlowSnapshot {
  const key = flowStorageKey(clinicId);
  const raw = localStorage.getItem(key);

  if (raw) {
    try {
      const parsed = JSON.parse(raw);
      if (parsed?.nodes?.length) return { nodes: parsed.nodes, edges: parsed.edges ?? [] };
    } catch {
      // Ignore parse errors and use default template
    }
  }

  // seed
  localStorage.setItem(key, JSON.stringify(INITIAL_TEMPLATE));
  return INITIAL_TEMPLATE;
}

export function saveFlow(clinicId: string, snapshot: FlowSnapshot) {
  const key = flowStorageKey(clinicId);
  localStorage.setItem(key, JSON.stringify(snapshot));
}

export function resetFlow(clinicId: string) {
  saveFlow(clinicId, INITIAL_TEMPLATE);
  return INITIAL_TEMPLATE;
}
