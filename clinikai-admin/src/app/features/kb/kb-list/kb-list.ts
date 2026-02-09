import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClinicKbEntry, PagedResponse } from '../kb.models';
import { KB_CATEGORIES } from '../kb.categories';
import { KbService } from '../kb.service';


@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './kb-list.html',
  styleUrl: './kb-list.css',
})
export class KbList {
  private kb = inject(KbService);

  categories = KB_CATEGORIES;

  // filtros
  q = '';
  category = '';
  enabled = ''; // '', 'true', 'false'
  tag = '';

  // paginação
  page = 0;
  size = 10;

  // ✅ signals
  loading = signal(false);
  error = signal<string | null>(null);
  data = signal<PagedResponse<ClinicKbEntry> | null>(null);

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading.set(true);
    this.error.set(null);

    const enabledBool =
      this.enabled === '' ? undefined : this.enabled === 'true';

    this.kb
      .list({
        page: this.page,
        size: this.size,
        sort: 'updatedAt,desc',
        enabled: enabledBool,
        category: this.category || undefined,
        tag: this.tag || undefined,
        q: this.q || undefined,
      })
      .subscribe({
        next: (res) => {
          this.data.set(res);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Não foi possível carregar a base de conhecimento.');
          this.loading.set(false);
        },
      });
  }

  applyFilters() {
    this.page = 0;
    this.load();
  }

  clearFilters() {
    this.q = '';
    this.category = '';
    this.enabled = '';
    this.tag = '';
    this.page = 0;
    this.load();
  }

  prevPage() {
    const d = this.data();
    if (!d?.page.hasPrevious) return;
    this.page--;
    this.load();
  }

  nextPage() {
    const d = this.data();
    if (!d?.page.hasNext) return;
    this.page++;
    this.load();
  }

  toggleEnabled(item: ClinicKbEntry) {
    const newValue = !item.enabled;
    item.enabled = newValue;

    this.kb.setEnabled(item.id, newValue).subscribe({
      error: () => {
        item.enabled = !newValue;
        alert('Falha ao alterar status.');
      },
    });
  }

  remove(item: ClinicKbEntry) {
    const ok = confirm(`Excluir KB "${item.title}"?`);
    if (!ok) return;

    this.kb.delete(item.id).subscribe({
      next: () => this.load(),
      error: () => alert('Falha ao excluir.'),
    });
  }

  short(text: string, max = 90) {
    const t = (text ?? '').trim();
    return t.length > max ? t.slice(0, max) + '…' : t;
  }
}
