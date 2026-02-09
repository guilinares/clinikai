import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { KbService } from '../kb.service';
import { KB_CATEGORIES } from '../kb.categories';


@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './kb-form.html',
  styleUrl: './kb-form.css',
})
export class KbForm {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private kb = inject(KbService);

  categories = KB_CATEGORIES;

  id: string | null = null;
  // ✅ signals
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  // ✅ form como signals (pra tela atualizar sempre)
  title = signal('');
  content = signal('');
  category = signal('FAQ');
  enabled = signal(true);

  tags = signal<string[]>([]);
  tagInput = signal('');

  get isEdit() {
    return !!this.id;
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.id = id;
      this.loadForEdit(id);
    }
  }

  loadForEdit(id: string) {
    this.loading.set(true);
    this.error.set(null);
    console.log('Loading KB for edit:', id);
    this.kb.getById(id).subscribe({
      next: (item) => {
        console.log('Loaded KB item:', item);
        this.title.set(item.title ?? '');
        this.content.set(item.content ?? '');
        this.category.set(item.category ?? 'FAQ');
        this.enabled.set(!!item.enabled);
        this.tags.set(Array.isArray(item.tags) ? [...item.tags] : []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Não foi possível carregar este KB para edição.');
        console.error('Failed to load KB item for edit:', id);
        this.loading.set(false);
      },
    });
  }

  addTag() {
    const raw = (this.tagInput() ?? '').trim();
    if (!raw) return;

    const tag = raw.toLowerCase().replace(/\s+/g, ' ').slice(0, 50);
    if (!tag) return;

    const current = this.tags();

    if (current.includes(tag)) {
      this.tagInput.set('');
      return;
    }
    if (current.length >= 20) {
      alert('Limite de 20 tags.');
      return;
    }

    this.tags.set([...current, tag]);
    this.tagInput.set('');
  }

  removeTag(tag: string) {
    this.tags.set(this.tags().filter((t) => t !== tag));
  }

  onTagKeydown(ev: KeyboardEvent) {
    if (ev.key === 'Enter' || ev.key === ',') {
      ev.preventDefault();
      this.addTag();
    }
    if (ev.key === 'Backspace' && !this.tagInput() && this.tags().length) {
      this.tags.set(this.tags().slice(0, -1));
    }
  }

  cancel() {
    this.router.navigateByUrl('/admin/kb');
  }

  save() {
    this.error.set(null);

    if (!this.title().trim()) {
      this.error.set('Título é obrigatório.');
      return;
    }
    if (!this.content().trim()) {
      this.error.set('Conteúdo é obrigatório.');
      return;
    }
    if (!this.category()) {
      this.error.set('Categoria é obrigatória.');
      return;
    }

    this.saving.set(true);

    const payload = {
      title: this.title().trim().slice(0, 200),
      content: this.content().trim(),
      category: this.category(),
      tags: this.tags(),
      enabled: this.enabled(),
    };

    const req$ = this.isEdit
      ? this.kb.update(this.id!, payload)
      : this.kb.create(payload);

    req$.subscribe({
      next: () => {
        this.saving.set(false);
        this.router.navigateByUrl('/admin/kb');
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(
          err?.error?.message ??
            'Falha ao salvar KB. Verifique os dados e tente novamente.'
        );
      },
    });
  }
}
