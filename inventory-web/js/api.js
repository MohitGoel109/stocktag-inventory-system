/* ============================================================
   API client — talks to the Spring Boot backend
   ============================================================ */

const API = (() => {
  // Adjust this if your API runs somewhere else (e.g. in production
  // behind a reverse proxy at /api already, you can set this to "").
  const BASE_URL = window.INVENTORY_API_BASE_URL || 'https://stocktag-inventory-system-production.up.railway.app/api';

  function getToken() {
    return localStorage.getItem('inv_token');
  }

  function setSession({ token, name, email, role, userId }) {
    localStorage.setItem('inv_token', token);
    localStorage.setItem('inv_user', JSON.stringify({ name, email, role, userId }));
  }

  function clearSession() {
    localStorage.removeItem('inv_token');
    localStorage.removeItem('inv_user');
  }

  function getCurrentUser() {
    const raw = localStorage.getItem('inv_user');
    return raw ? JSON.parse(raw) : null;
  }

  async function request(path, { method = 'GET', body, auth = true } = {}) {
    const headers = { 'Content-Type': 'application/json' };
    if (auth) {
      const token = getToken();
      if (token) headers['Authorization'] = `Bearer ${token}`;
    }

    let response;
    try {
      response = await fetch(`${BASE_URL}${path}`, {
        method,
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined,
      });
    } catch (networkErr) {
      throw new ApiError(0, 'Cannot reach the server. Check your connection or that the API is running.', null);
    }

    if (response.status === 401) {
      clearSession();
      if (!location.pathname.endsWith('index.html') && location.pathname !== '/') {
        location.href = 'index.html';
      }
      throw new ApiError(401, 'Your session has expired. Please log in again.', null);
    }

    if (response.status === 204) return null;

    const isJson = response.headers.get('content-type')?.includes('application/json');
    const data = isJson ? await response.json() : null;

    if (!response.ok) {
      const message = data?.message || `Request failed (${response.status})`;
      throw new ApiError(response.status, message, data?.fieldErrors || null);
    }

    return data;
  }

  return {
    // auth
    login: (email, password) => request('/auth/login', { method: 'POST', body: { email, password }, auth: false }),
    getCurrentUser,
    setSession,
    clearSession,
    isLoggedIn: () => !!getToken(),

    // dashboard
    getDashboardStats: () => request('/dashboard/stats'),

    // categories
    listCategories: () => request('/categories'),
    createCategory: (dto) => request('/categories', { method: 'POST', body: dto }),
    updateCategory: (id, dto) => request(`/categories/${id}`, { method: 'PUT', body: dto }),
    deleteCategory: (id) => request(`/categories/${id}`, { method: 'DELETE' }),

    // products
    listProducts: (search) => request(`/products${search ? `?search=${encodeURIComponent(search)}` : ''}`),
    createProduct: (dto) => request('/products', { method: 'POST', body: dto }),
    updateProduct: (id, dto) => request(`/products/${id}`, { method: 'PUT', body: dto }),
    deleteProduct: (id) => request(`/products/${id}`, { method: 'DELETE' }),

    // customers
    listCustomers: (search) => request(`/customers${search ? `?search=${encodeURIComponent(search)}` : ''}`),
    createCustomer: (dto) => request('/customers', { method: 'POST', body: dto }),
    updateCustomer: (id, dto) => request(`/customers/${id}`, { method: 'PUT', body: dto }),
    deleteCustomer: (id) => request(`/customers/${id}`, { method: 'DELETE' }),

    // users (admin only)
    listUsers: () => request('/users'),
    createUser: (dto) => request('/users', { method: 'POST', body: dto }),
    updateUser: (id, dto) => request(`/users/${id}`, { method: 'PUT', body: dto }),
    deleteUser: (id) => request(`/users/${id}`, { method: 'DELETE' }),

    // orders
    listOrders: () => request('/orders'),
    getOrder: (id) => request(`/orders/${id}`),
    placeOrder: (dto) => request('/orders', { method: 'POST', body: dto }),
  };
})();

class ApiError extends Error {
  constructor(status, message, fieldErrors) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}
