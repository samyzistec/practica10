export const $ = (sel, ctx=document) => ctx.querySelector(sel);
export const $$ = (sel, ctx=document) => Array.from(ctx.querySelectorAll(sel));
export function toast(msg, type="primary") {
  const t = $("#toast"); const body = $("#toast-body");
  t.className = `toast align-items-center text-bg-${type} border-0`; body.textContent = msg;
  bootstrap.Toast.getOrCreateInstance(t).show();
}
export function paginate(array, page, pageSize) {
  const total = array.length; const pages = Math.max(1, Math.ceil(total / pageSize));
  const start = (page - 1) * pageSize; return { data: array.slice(start, start + pageSize), total, pages };
}
export function renderPagination(container, pages, current, onClick) {
  container.innerHTML = ""; const ul = container;
  const mk = (label, page, active=false, disabled=false) => {
    const li = document.createElement("li"); li.className = `page-item ${active?"active":""} ${disabled?"disabled":""}`;
    const a = document.createElement("a"); a.className = "page-link"; a.href = "#"; a.textContent = label;
    a.addEventListener("click", (e)=>{e.preventDefault(); if(!disabled) onClick(page);});
    li.appendChild(a); ul.appendChild(li);
  };
  mk("«", Math.max(1, current-1), false, current===1);
  for (let p=1; p<=pages; p++) mk(String(p), p, p===current);
  mk("»", Math.min(pages, current+1), false, current===pages);
}
export function downloadCSV(filename, rows) {
  const esc = (v)=> v==null ? "" : `"${String(v).replace(/"/g,'""')}"`;
  const csv = rows.map(r => r.map(esc).join(",")).join("\n");
  const blob = new Blob([csv], {type: "text/csv;charset=utf-8;"});
  const url = URL.createObjectURL(blob); const a = document.createElement("a"); a.href = url; a.download = filename; a.click(); URL.revokeObjectURL(url);
}
export function printPDF(){ window.print(); }
