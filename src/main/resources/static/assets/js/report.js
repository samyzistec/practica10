// assets/js/report.js
import { API_BASE } from "./config.js";

document.addEventListener("DOMContentLoaded", () => {
  const btnPdf = document.getElementById("btn-export-pdf");
  if (!btnPdf) {
    console.error("[report] No se encontró #btn-export-pdf en el DOM");
    return;
  }
  btnPdf.addEventListener("click", onExportPDF);
});

async function onExportPDF() {
  try {
    ensureDeps(); // valida jsPDF + autoTable

    // Trae datos con cache-busting
    const [promResp, califs] = await Promise.all([
      fetchJSON("/api/calificaciones/promedio"),
      fetchJSON("/api/calificaciones"),
    ]);

    const promGeneral = isFinite(Number(promResp?.promedio))
      ? Number(promResp.promedio).toFixed(2)
      : "—";

    const { rows, resumen } = buildAveragesByCourse(califs);

    await buildPDF({ promGeneral, rows, resumen });
  } catch (err) {
    console.error("[report] Error:", err);
    alert("No se pudo generar el PDF: " + (err?.message || err));
  }
}

/* ================= Helpers ================= */

function ensureDeps() {
  // jsPDF UMD expone window.jspdf.jsPDF
  if (!window.jspdf || !window.jspdf.jsPDF) {
    throw new Error("Dependencia faltante: jsPDF no cargó.");
  }
  // autoTable añade doc.autoTable
  // (no siempre hay bandera global; validamos en tiempo de uso también)
}

async function fetchJSON(path) {
  const sep = path.includes("?") ? "&" : "?";
  const res = await fetch(API_BASE + path + sep + "_=" + Date.now(), {
    headers: { Accept: "application/json", "Cache-Control": "no-store" },
    cache: "no-store",
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

function buildAveragesByCourse(califs = []) {
  const agg = new Map();
  califs.forEach((c) => {
    const curso = c?.inscripcion?.curso?.nombre ?? "(Sin curso)";
    const nota = Number(c?.nota);
    if (!isFinite(nota)) return;
    const item =
      agg.get(curso) || { sum: 0, count: 0, min: Number.POSITIVE_INFINITY, max: Number.NEGATIVE_INFINITY };
    item.sum += nota;
    item.count += 1;
    item.min = Math.min(item.min, nota);
    item.max = Math.max(item.max, nota);
    agg.set(curso, item);
  });

  const rows = [];
  let totalCount = 0;
  agg.forEach((v, k) => {
    const avg = v.count ? v.sum / v.count : 0;
    rows.push([k, String(v.count), avg.toFixed(2), v.min.toFixed(2), v.max.toFixed(2)]);
    totalCount += v.count;
  });

  // Ordena por promedio desc
  rows.sort((a, b) => Number(b[2]) - Number(a[2]));

  return {
    rows,
    resumen: { cursos: agg.size, calificaciones: totalCount },
  };
}

async function buildPDF({ promGeneral, rows, resumen }) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ unit: "pt", format: "a4" });

  const marginX = 56; // ~2cm
  let y = 60;

  // Encabezado
  doc.setFont("helvetica", "bold");
  doc.setFontSize(18);
  doc.text("Reporte de Calificaciones", marginX, y);
  y += 22;

  doc.setFont("helvetica", "normal");
  doc.setFontSize(10);
  doc.text(`Generado: ${new Date().toLocaleString()}`, marginX, y);
  y += 24;

  // Promedio general
  doc.setFont("helvetica", "bold");
  doc.setFontSize(13);
  doc.text("Promedio general:", marginX, y);
  doc.setFont("helvetica", "normal");
  doc.text(String(promGeneral), marginX + 140, y);
  y += 18;

  // Resumen
  doc.setFont("helvetica", "bold");
  doc.text("Resumen:", marginX, y);
  doc.setFont("helvetica", "normal");
  doc.text(`Cursos: ${resumen.cursos}   •   Calificaciones: ${resumen.calificaciones}`, marginX + 80, y);
  y += 20;

  // Tabla: Promedio por curso
  doc.setFont("helvetica", "bold");
  doc.setFontSize(13);
  doc.text("Promedio por curso", marginX, y);
  y += 8;

  // Asegura que autoTable esté disponible
  if (typeof doc.autoTable !== "function") {
    throw new Error("jspdf-autotable no está disponible (doc.autoTable es undefined).");
  }

  doc.autoTable({
    startY: y,
    margin: { left: marginX, right: marginX },
    styles: { font: "helvetica", fontSize: 10, cellPadding: 6 },
    theme: "striped",
    head: [["Curso", "N", "Promedio", "Mín", "Máx"]],
    body: rows.length ? rows : [["(Sin datos)", "0", "—", "—", "—"]],
    headStyles: { fillColor: [33, 150, 243] }, // azul
    didDrawPage: () => {
      const str = `Página ${doc.internal.getNumberOfPages()}`;
      doc.setFontSize(9);
      doc.text(str, doc.internal.pageSize.getWidth() - marginX, doc.internal.pageSize.getHeight() - 16, {
        align: "right",
      });
    },
  });

  doc.save("reporte-calificaciones.pdf");
}
