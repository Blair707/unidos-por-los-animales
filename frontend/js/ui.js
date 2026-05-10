/* ============================================================
   ui.js - Utilidades de interfaz
   ============================================================ */

const UI = {
    // ---- Alertas ----
    alerta(contenedor, tipo, mensaje) {
        const div = document.createElement('div');
        div.className = `alerta alerta--${tipo}`;
        div.textContent = mensaje;
        contenedor.prepend(div);
        setTimeout(() => div.remove(), 5000);
    },

    // ---- Spinner ----
    mostrarSpinner(contenedor, mensaje = 'Cargando...') {
        contenedor.innerHTML = `
            <div class="flex-centrado" style="padding:3rem;gap:0.75rem">
                <div class="spinner"></div>
                <span class="texto-suave">${mensaje}</span>
            </div>`;
    },

    // ---- Estado vacío ----
    vacio(contenedor, mensaje = 'No hay elementos para mostrar.') {
        contenedor.innerHTML = `
            <div class="vacio"><p class="vacio__texto">${mensaje}</p></div>`;
    },

    // ---- Modal ----
    abrirModal(htmlContenido) {
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';
        overlay.innerHTML = `<div class="modal">${htmlContenido}</div>`;
        overlay.addEventListener('click', e => {
            if (e.target === overlay) overlay.remove();
        });
        document.body.appendChild(overlay);
        return overlay;
    },

    cerrarModales() {
        document.querySelectorAll('.modal-overlay').forEach(m => m.remove());
    },

    // ---- Navbar ----
    actualizarNavbar() {
        const nav       = document.getElementById('nav-usuario');
        const navLogin  = document.getElementById('nav-login');
        const navLogout = document.getElementById('nav-logout');

        if (!nav) return;

        if (Api.estaAutenticado()) {
            const email = localStorage.getItem('usuario_email') || '';
            nav.querySelector('.usuario-email') && (nav.querySelector('.usuario-email').textContent = email);
            if (navLogin)  navLogin.classList.add('oculto');
            if (navLogout) navLogout.classList.remove('oculto');
            nav.classList.remove('oculto');
        } else {
            if (navLogin)  navLogin.classList.remove('oculto');
            if (navLogout) navLogout.classList.add('oculto');
            nav.classList.add('oculto');
        }
    },

    // ---- Formatear fecha ----
    formatearFecha(fechaStr) {
        if (!fechaStr) return '-';
        return new Date(fechaStr).toLocaleDateString('es-CL', {
            day: '2-digit', month: '2-digit', year: 'numeric'
        });
    },

    // ---- Edad legible ----
    formatearEdad(meses) {
        if (meses < 12) return `${meses} mes${meses !== 1 ? 'es' : ''}`;
        const años = Math.floor(meses / 12);
        return `${años} año${años !== 1 ? 's' : ''}`;
    },

    // ---- Badge ---- 
    badge(estado) {
        const mapa = {
            DISPONIBLE:  'disponible',
            EN_PROCESO:  'en-proceso',
            ADOPTADO:    'adoptado',
            PENDIENTE:   'pendiente',
            EN_REVISION: 'en-revision',
            APROBADA:    'aprobada',
            RECHAZADA:   'rechazada'
        };
        const clase = mapa[estado] || 'pendiente';
        const texto = estado.replace('_', ' ');
        return `<span class="badge badge--${clase}">${texto}</span>`;
    }
};

// Inicializar navbar en todas las páginas
document.addEventListener('DOMContentLoaded', () => UI.actualizarNavbar());
