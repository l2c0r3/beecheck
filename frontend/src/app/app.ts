import {Component, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Navbar} from './shared/components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar],
  template: `
    <app-navbar></app-navbar>
    <main class="main">
      <div class="content">
        <h1>Hello my friend</h1>
      </div>
    </main>
  `,
})
export class App {
  protected readonly title = signal('frontend');
}
