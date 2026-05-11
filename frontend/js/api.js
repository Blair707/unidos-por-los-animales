/* ============================================================
   api.js - Cliente HTTP para la API REST
   ============================================================ */

const API_BASE = 'https://unidos-por-los-animales-production.up.railway.app/api';

const Api = {
    // ---- Token ----
    getToken() {
        return localStorage.getItem('jwt_token');
    },
    setToken(token) {
        localStorage.setItem('jwt_token', token);
    },
    removeToken() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('usuario_email');
    },
    estaAutenticado() {
        return !!this.getToken();
    },

    // ---- Cabeceras ----
    _headers(conAuth = true) {
        const h = { 'Content-Type': 'application/json' };
        if (conAuth && this.getToken()) {
            h['Authorization'] = `Bearer ${this.getToken()}`;
        }
        return h;
    },

    // ---- Petición base ----
    async _fetch(endpoint, options = {}) {
        const res = await fetch(`${API_BASE}${endpoint}`, options);
        if (res.status === 204) return null;
        const data = await res.json().catch(() => ({}));
        if (!res.ok) {
            throw new Error(data.message || `Error ${res.status}`);
        }
        return data;
    },

    // ---- Auth ----
    async login(email, password) {
        const data = await this._fetch('/auth/login', {
            method: 'POST',
            headers: this._headers(false),
            body: JSON.stringify({ email, password })
        });
        this.setToken(data.token);
        localStorage.setItem('usuario_email', email);
        return data;
    },

    async registro(payload) {
        return this._fetch('/auth/registro', {
            method: 'POST',
            headers: this._headers(false),
            body: JSON.stringify(payload)
        });
    },

    logout() {
        this.removeToken();
        window.location.href = 'index.html';
    },

    // ---- Mascotas ----
    async getMascotas(especie = null) {
        const qs = especie ? `?especie=${especie}` : '';
        return this._fetch(`/mascotas${qs}`, { headers: this._headers(false) });
    },

    async getMascota(id) {
        return this._fetch(`/mascotas/${id}`, { headers: this._headers(false) });
    },

    // ---- Solicitudes ----
    async crearSolicitud(payload) {
        return this._fetch('/solicitudes', {
            method: 'POST',
            headers: this._headers(),
            body: JSON.stringify(payload)
        });
    },

    async getMisSolicitudes() {
        return this._fetch('/solicitudes/mis-solicitudes', { headers: this._headers() });
    },

    async getSolicitudes(estado = null) {
        const qs = estado ? `?estado=${estado}` : '';
        return this._fetch(`/solicitudes${qs}`, { headers: this._headers() });
    },

    async cambiarEstadoSolicitud(id, estado) {
        return this._fetch(`/solicitudes/${id}/estado?estado=${estado}`, {
            method: 'PATCH',
            headers: this._headers()
        });
    },

    // ---- Mensajes ----
    async enviarMensaje(payload) {
        return this._fetch('/mensajes', {
            method: 'POST',
            headers: this._headers(),
            body: JSON.stringify(payload)
        });
    },

    async getBandejaEntrada() {
        return this._fetch('/mensajes/entrada', { headers: this._headers() });
    },

    async getBandejaSalida() {
        return this._fetch('/mensajes/salida', { headers: this._headers() });
    },

    async getNoLeidos() {
        return this._fetch('/mensajes/no-leidos', { headers: this._headers() });
    }
};
