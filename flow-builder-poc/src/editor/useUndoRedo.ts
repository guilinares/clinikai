import { useCallback, useState } from "react";
import type { FlowSnapshot } from "../domain/flow.types";

export function useUndoRedo(limit = 50) {
  const [history, setHistory] = useState<FlowSnapshot[]>([]);
  const [future, setFuture] = useState<FlowSnapshot[]>([]);

  const push = useCallback((snap: FlowSnapshot) => {
    setHistory((prev) => {
      const updated = [...prev, snap];
      if (updated.length > limit) updated.shift();
      return updated;
    });
    setFuture([]);
  }, [limit]);

  const undo = useCallback((current: FlowSnapshot, apply: (s: FlowSnapshot) => void) => {
    setHistory((prev) => {
      if (prev.length === 0) return prev;
      const last = prev[prev.length - 1];
      setFuture((f) => [current, ...f]);
      apply(last);
      return prev.slice(0, -1);
    });
  }, []);

  const redo = useCallback((current: FlowSnapshot, apply: (s: FlowSnapshot) => void) => {
    setFuture((prev) => {
      if (prev.length === 0) return prev;
      const next = prev[0];
      setHistory((h) => [...h, current]);
      apply(next);
      return prev.slice(1);
    });
  }, []);

  return { history, future, push, undo, redo };
}
