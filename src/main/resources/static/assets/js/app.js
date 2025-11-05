// assets/js/app.js
import { API_BASE } from "./config.js";
import { $, $$, toast, paginate, renderPagination, downloadCSV } from "./utils.js";

/* =========================
   Configuración de entidades
   ========================= */
const ENTITIES = {
  estudiantes: {
    title: "Estudiante",
    endpoint: "/api/estudiantes",
    columns: [
      { key: "id", label: "ID" },
      { key: "nombre", label: "Nombre" },
      { key: "email", label: "Email" },
      { key: "fechaIngreso", label: "Fecha ingreso" },
    ],
    form: [
      { name: "nombre", label: "Nombre", type: "text", required: true, maxLength: 100 },
      { name: "email", label: "Email", type: "email", required: true, maxLength: 120 },
      { name: "fechaIngreso", label: "Fecha de ingreso", type: "date", required: true },
    ],
  },
  instructores: {
    title: "Instructor",
    endpoint: "/api/instructores",
    columns: [
      { key: "id", label: "ID" },
      { key: "nombre", label: "Nombre" },
      { key: "email", label: "Email" },
    ],
    form: [
      { name: "nombre", label: "Nombre", type: "text", required: true, maxLength: 100 },
      { name: "email", label: "Email", type: "email", required: true, maxLength: 120 },
    ],
  },
  cursos: {
    title: "Curso",
    endpoint: "/api/cursos",
    columns: [
      { key: "id", label: "ID" },
      { key: "clave", label: "Clave" },
      { key: "nombre", label: "Nombre" },
      { key: "creditos", label: "Créditos" },
      { key: "instructor", label: "Instructor", render: (v) => v?.nombre ?? "" },
    ],
    form: [
      { name: "clave", label: "Clave", type: "text", required: true, maxLength: 20 },
      { name: "nombre", label: "Nombre", type: "text", required: true, maxLength: 120 },
      { name: "creditos", label: "Créditos", type: "number", required: true, min: 1, max: 10 },
      { name: "instructor", label: "Instructor", type: "select", required: false, source: "/api/instructores", optionLabel: "nombre", optionValue: "id" },
    ],
  },
  inscripciones: {
    title: "Inscripción",
    endpoint: "/api/inscripciones",
    columns: [
      { key: "id", label: "ID" },
      { key: "estudiante", label: "Estudiante", render: (v) => v?.nombre ?? "" },
      { key: "curso", label: "Curso", render: (v) => v?.nombre ?? "" },
      { key: "periodo", label: "Periodo" },
    ],
    form: [
      { name: "estudiante", label: "Estudiante", type: "select", required: true, source: "/api/estudiantes", optionLabel: "nombre", optionValue: "id" },
      { name: "curso", label: "Curso", type: "select", required: true, source: "/api/cursos", optionLabel: "nombre", optionValue: "id" },
      { name: "periodo", label: "Periodo", type: "text", required: true, maxLength: 20 },
    ],
  },
  calificaciones: {
    title: "Calificación",
    endpoint: "/api/calificaciones",
    columns: [
      { key: "id", label: "ID" },
      { key: "inscripcion", label: "Inscripción", render: (v) => (v ? `${v.id} - ${v.estudiante?.nombre ?? ""} / ${v.curso?.nombre ?? ""}` : "") },
      { key: "nota", label: "Nota" },
    ],
    form: [
      {
        name: "inscripcion",
        label: "Inscripción",
        type: "select",
        required: true,
        source: "/api/inscripciones",
        optionLabel: (x) => `${x.id} - ${x.estudiante?.nombre ?? ""} / ${x.curso?.nombre ?? ""}`,
        optionValue: "id",
      },
      { name: "nota", label: "Nota (0-100)", type: "number", required: true, min: 0, max: 100, step: "0.01" },
    ],
  },
};

/* ============ Estado global ============ */
const state = {
  active: "estudiantes",
  data: { estudiantes: [], instructores: [], cursos: [], inscripciones: [], calificaciones: [] },
  page: { estudiantes: 1, instructores: 1, cursos: 1, inscripciones: 1, calificaciones: 1 },
  pageSize: 8,
  search: { estudiantes: "", instructores: "", cursos: "", inscripciones: "", calificaciones: "" },
};

/* ================= Helpers de API ================= */
async function apiGet(url) {
  // Cache-busting y no-store para evitar cacheos
  const sep = url.includes("?") ? "&" : "?";
  const r = await fetch(API_BASE + url + sep + "_=" + Date.now(), {
    headers: { Accept: "application/json", "Cache-Control": "no-store" },
    cache: "no-store",
  });
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

async function apiSend(method, url, body) {
  const r = await fetch(API_BASE + url, {
    method,
    headers: { "Content-Type": "application/json", "Cache-Control": "no-store" },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (r.status === 204) return null;
  const txt = await r.text();
  if (!r.ok) throw new Error(txt || r.statusText);
  return txt ? JSON.parse(txt) : null;
}

/* ============== Render de listas ============== */
function renderList(key) {
  const cfg = ENTITIES[key];
  const wrap = document.getElementById(`table-${key}-wrapper`);
  const pag = document.getElementById(`pagination-${key}`);
  if (!wrap) return;

  const q = state.search[key] || "";
  const filtered = state.data[key].filter((row) => JSON.stringify(row).toLowerCase().includes(q));
  const { data, pages } = paginate(filtered, state.page[key], state.pageSize);

  const ths = cfg.columns.map((c) => `<th>${c.label}</th>`).join("") + '<th class="text-end">Acciones</th>';
  const trs = data
    .map((row) => {
      const tds = cfg.columns
        .map((c) => {
          const value = c.render ? c.render(row[c.key]) : row[c.key];
          return `<td>${value ?? ""}</td>`;
        })
        .join("");
      return `<tr>${tds}<td class="text-end table-actions">
        <button class="btn btn-sm btn-outline-secondary me-1" data-action="view" data-id="${row.id}" data-entity="${key}" title="Ver"><i class="bi bi-eye"></i></button>
        <button class="btn btn-sm btn-outline-primary me-1" data-action="edit" data-id="${row.id}" data-entity="${key}" title="Editar"><i class="bi bi-pencil"></i></button>
        <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${row.id}" data-entity="${key}" title="Eliminar"><i class="bi bi-trash"></i></button>
      </td></tr>`;
    })
    .join("");

  wrap.innerHTML = `<table class="table table-hover align-middle">
    <thead><tr>${ths}</tr></thead>
    <tbody>${trs || `<tr><td colspan="${cfg.columns.length + 1}" class="text-center text-muted">Sin datos</td></tr>`}</tbody>
  </table>`;

  renderPagination(pag, pages, state.page[key], (p) => {
    state.page[key] = p;
    renderList(key);
  });

  wrap.querySelectorAll("button[data-action]").forEach((btn) => {
    const id = Number(btn.dataset.id);
    const a = btn.dataset.action;
    if (a === "view") btn.addEventListener("click", () => openView(key, id));
    if (a === "edit") btn.addEventListener("click", () => openEdit(key, id));
    if (a === "delete") btn.addEventListener("click", () => confirmDelete(key, id));
  });
}

async function loadEntity(key) {
  try {
    state.data[key] = (await apiGet(ENTITIES[key].endpoint)) || [];
    renderList(key);
  } catch (err) {
    console.error(err);
    toast(`Error al cargar ${key}: ${err.message}`, "danger");
  }
}

/* ============= CRUD / Modales ============= */
function buildFormHTML(key, data = {}) {
  const cfg = ENTITIES[key];
  return cfg.form
    .map((f) => {
      const id = `f-${key}-${f.name}`;
      const value = resolveValue(f, data);
      const attrs = [
        `id="${id}"`,
        `name="${f.name}"`,
        f.type !== "select" ? `type="${f.type}"` : "",
        f.required ? "required" : "",
        f.maxLength ? `maxlength="${f.maxLength}"` : "",
        f.min != null ? `min="${f.min}"` : "",
        f.max != null ? `max="${f.max}"` : "",
        f.step ? `step="${f.step}"` : "",
      ]
        .filter(Boolean)
        .join(" ");
      const help = `<div class="invalid-feedback">Dato inválido o incompleto.</div>`;
      if (f.type === "select") {
        return `<div class="mb-3"><label for="${id}" class="form-label">${f.label}</label><select class="form-select" ${attrs}></select>${help}</div>`;
      }
      return `<div class="mb-3"><label for="${id}" class="form-label">${f.label}</label><input class="form-control" ${attrs} value="${value ?? ""}"/>${help}</div>`;
    })
    .join("");
}

function resolveValue(field, data) {
  const v = data?.[field.name];
  if (field.type === "select") return v?.id ?? v ?? "";
  return v ?? "";
}

async function fillSelects(key, data) {
  const cfg = ENTITIES[key];
  for (const f of cfg.form) {
    if (f.type !== "select") continue;
    const sel = document.getElementById(`f-${key}-${f.name}`);
    sel.innerHTML = `<option value="">Seleccione...</option>`;
    try {
      const list = await apiGet(f.source);
      list.forEach((item) => {
        const label = typeof f.optionLabel === "function" ? f.optionLabel(item) : item[f.optionLabel];
        const val = item[f.optionValue];
        const opt = document.createElement("option");
        opt.value = val;
        opt.textContent = label;
        if (data?.[f.name]?.id === val || data?.[f.name] === val) opt.selected = true;
        sel.appendChild(opt);
      });
    } catch (err) {
      toast(`Error al cargar opciones: ${err.message}`, "danger");
    }
  }
}

function openCreate(key) {
  const modal = new bootstrap.Modal(document.getElementById("crudModal"));
  $("#crudModalLabel").textContent = `Nuevo ${ENTITIES[key].title}`;
  const form = $("#crudForm");
  form.dataset.mode = "create";
  form.dataset.entity = key;
  form.dataset.id = "";
  form.innerHTML = buildFormHTML(key, {});
  form.classList.remove("was-validated");
  fillSelects(key, {});
  $("#btn-save").style.display = "";
  modal.show();
}

function openView(key, id) {
  const row = state.data[key].find((x) => x.id === id);
  if (!row) return;
  const modal = new bootstrap.Modal(document.getElementById("crudModal"));
  $("#crudModalLabel").textContent = `${ENTITIES[key].title} • Detalle`;
  const form = $("#crudForm");
  form.dataset.mode = "view";
  form.dataset.entity = key;
  form.dataset.id = id;
  form.innerHTML = buildFormHTML(key, row);
  form.querySelectorAll("input,select,button").forEach((el) => (el.disabled = true));
  $("#btn-save").style.display = "none";
  fillSelects(key, row);
  modal.show();
}

function openEdit(key, id) {
  const row = state.data[key].find((x) => x.id === id);
  if (!row) return;
  const modal = new bootstrap.Modal(document.getElementById("crudModal"));
  $("#crudModalLabel").textContent = `Editar ${ENTITIES[key].title}`;
  const form = $("#crudForm");
  form.dataset.mode = "edit";
  form.dataset.entity = key;
  form.dataset.id = id;
  form.innerHTML = buildFormHTML(key, row);
  form.classList.remove("was-validated");
  $("#btn-save").style.display = "";
  fillSelects(key, row);
  modal.show();
}

async function confirmDelete(key, id) {
  if (!confirm("¿Eliminar registro definitivamente?")) return;
  try {
    await apiSend("DELETE", `${ENTITIES[key].endpoint}/${id}`);
    toast("Eliminado correctamente", "success");

    if (key === "instructores") await loadEntity("cursos");
    if (key === "estudiantes" || key === "cursos") await loadEntity("inscripciones");
    if (key === "inscripciones") await loadEntity("calificaciones");
    await loadEntity(key);
  } catch (err) {
    toast(`Error al eliminar: ${err.message}`, "danger");
  }
}

document.getElementById("crudForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const form = e.target;
  form.classList.add("was-validated");
  if (!form.checkValidity()) return toast("Por favor corrige los campos.", "warning");

  const key = form.dataset.entity;
  const mode = form.dataset.mode;
  const id = form.dataset.id ? Number(form.dataset.id) : null;
  const payload = buildPayload(key);

  try {
    if (mode === "create") await apiSend("POST", ENTITIES[key].endpoint, payload);
    else if (mode === "edit") await apiSend("PUT", `${ENTITIES[key].endpoint}/${id}`, payload);

    bootstrap.Modal.getInstance(document.getElementById("crudModal"))?.hide();
    toast("Guardado correctamente", "success");

    if (key === "instructores") await loadEntity("cursos");
    if (key === "estudiantes" || key === "cursos") await loadEntity("inscripciones");
    if (key === "inscripciones") await loadEntity("calificaciones");
    await loadEntity(key);
  } catch (err) {
    toast(`Error al guardar: ${err.message}`, "danger");
  }
});

function buildPayload(key) {
  const cfg = ENTITIES[key];
  const data = {};
  for (const f of cfg.form) {
    const el = document.getElementById(`f-${key}-${f.name}`);
    let val = el.value;
    if (f.type === "number") val = val === "" ? null : Number(val);
    if (f.type === "select") val = val ? { id: Number(val) } : null;
    data[f.name] = val;
  }
  if (key === "estudiantes") return { nombre: data.nombre, email: data.email, fechaIngreso: data.fechaIngreso };
  if (key === "instructores") return { nombre: data.nombre, email: data.email };
  if (key === "cursos") return { clave: data.clave, nombre: data.nombre, creditos: data.creditos, instructor: data.instructor };
  if (key === "inscripciones") return { estudiante: data.estudiante, curso: data.curso, periodo: data.periodo };
  if (key === "calificaciones") return { inscripcion: data.inscripcion, nota: data.nota };
  return data;
}

/* ======================= Inicialización ======================= */
document.addEventListener("DOMContentLoaded", () => {
  // Carga inicial de todas las entidades
  ["instructores", "estudiantes", "cursos", "inscripciones", "calificaciones"].forEach(loadEntity);

  // Tabs
  $$("#entityTabs button[data-bs-toggle='tab']").forEach((btn) => {
    btn.addEventListener("shown.bs.tab", (e) => {
      const id = e.target.id.replace("-tab", "");
      state.active = id;
      renderList(id);
    });
  });

  // Buscadores y botón "Nuevo"
  Object.keys(ENTITIES).forEach((key) => {
    const input = document.getElementById(`search-${key}`);
    input?.addEventListener("input", () => {
      state.search[key] = input.value.trim().toLowerCase();
      state.page[key] = 1;
      renderList(key);
    });
    document
      .querySelector(`[data-entity='${key}'][data-action='open-create']`)
      ?.addEventListener("click", () => openCreate(key));
  });

  // Exportar CSV (PDF lo maneja assets/js/report.js)
  document.getElementById("btn-export-csv").addEventListener("click", () => {
    const key = state.active;
    const cfg = ENTITIES[key];
    const rows = [[...cfg.columns.map((c) => c.label)]];
    state.data[key].forEach((row) => {
      rows.push(cfg.columns.map((c) => (c.render ? c.render(row[c.key]) : row[c.key]) ?? ""));
    });
    downloadCSV(`${key}.csv`, rows);
  });

  // Refrescar calificaciones (solo CRUD; sin gráficos)
  document.getElementById("btnRefrescarCal")?.addEventListener("click", async () => {
    await loadEntity("calificaciones");
  });
});
