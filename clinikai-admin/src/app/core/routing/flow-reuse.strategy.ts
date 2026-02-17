import { ActivatedRouteSnapshot, DetachedRouteHandle, RouteReuseStrategy } from '@angular/router';

export class FlowReuseStrategy implements RouteReuseStrategy {
  private storedHandles = new Map<string, DetachedRouteHandle>();

  // muda isso pro caminho exato da sua rota do builder
  private readonly reusePaths = new Set<string>(['admin/flow']);

  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    const path = this.getFullPath(route);
    return this.reusePaths.has(path);
  }

  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle | null): void {
    if (!handle) return;
    const path = this.getFullPath(route);
    if (this.reusePaths.has(path)) {
      this.storedHandles.set(path, handle);
    }
  }

  shouldAttach(route: ActivatedRouteSnapshot): boolean {
    const path = this.getFullPath(route);
    return this.storedHandles.has(path);
  }

  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle | null {
    const path = this.getFullPath(route);
    return this.storedHandles.get(path) ?? null;
  }

  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    // comportamento padrão: reutiliza se for a mesma rota
    return future.routeConfig === curr.routeConfig;
  }

  private getFullPath(route: ActivatedRouteSnapshot): string {
    // monta algo como "admin/flow"
    const segments: string[] = [];
    let r: ActivatedRouteSnapshot | null = route;

    while (r) {
      const url = r.url?.map(s => s.path).filter(Boolean) ?? [];
      segments.unshift(...url);
      r = r.parent ?? null;
    }

    return segments.join('/');
  }
}
