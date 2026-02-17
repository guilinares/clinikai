import { Component } from '@angular/core';

@Component({
  selector: 'app-flow-embed',
  standalone: true,
  template: `
    <div class="card" style="height: calc(100vh - 140px); overflow: hidden;">
      <iframe
        src="/assets/flow/index.html"
        style="width: 100%; height: 100%; border: 0;"
      ></iframe>
    </div>
  `,
})
export class FlowEmbed {}
