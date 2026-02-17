import { Component } from '@angular/core';

@Component({
  selector: 'app-flow-poc',
  imports: [],
  templateUrl: './flow-poc.html',
  styleUrl: './flow-poc.css',
})
export class FlowPoc {

  ngOnInit() {
    console.log('Flow page initialized');
  }

  ngOnDestroy() {
    console.log('Flow page destroyed');
  }
}
