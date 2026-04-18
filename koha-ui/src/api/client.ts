/**
 * API client for Koha backend services.
 * All calls are routed through the intranet gateway (proxied via /api).
 */

const API_BASE = '/api/v1';

// ── Auth token storage ────────────────────────────────────────────────────────

let _token: string | null = null;

export function getToken(): string | null {
  return _token ?? sessionStorage.getItem('koha_token');
}

export function setToken(token: string): void {
  _token = token;
  sessionStorage.setItem('koha_token', token);
}

export function clearToken(): void {
  _token = null;
  sessionStorage.removeItem('koha_token');
}

// ── HTTP helpers ──────────────────────────────────────────────────────────────

function authHeaders(): HeadersInit {
  const token = getToken();
  return {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const body = await res.text();
    throw new Error(`HTTP ${res.status}: ${body}`);
  }
  if (res.status === 204) return undefined as unknown as T;
  return res.json() as Promise<T>;
}

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'GET',
    headers: authHeaders(),
  });
  return handleResponse<T>(res);
}

async function post<T>(path: string, body: unknown): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  return handleResponse<T>(res);
}

async function put<T>(path: string, body: unknown): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  return handleResponse<T>(res);
}

async function del(path: string): Promise<void> {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  return handleResponse<void>(res);
}

// ── Auth ──────────────────────────────────────────────────────────────────────

import type {
  AcquisitionsHomeDto,
  BiblioDto,
  CheckoutDto,
  ItemDto,
  LoginRequest,
  Page,
  PatronDto,
  TokenResponse,
} from '../types';

export async function login(req: LoginRequest): Promise<TokenResponse> {
  return post<TokenResponse>('/auth/login', req);
}

// ── Catalog ───────────────────────────────────────────────────────────────────

export async function listBiblios(
  query?: string,
  page = 0,
  size = 50
): Promise<Page<BiblioDto>> {
  const qs = new URLSearchParams();
  if (query) qs.set('q', query);
  qs.set('page', String(page));
  qs.set('size', String(size));
  return get<Page<BiblioDto>>(`/biblios?${qs}`);
}

export async function getBiblio(id: number): Promise<BiblioDto> {
  return get<BiblioDto>(`/biblios/${id}`);
}

export async function createBiblio(dto: BiblioDto): Promise<BiblioDto> {
  return post<BiblioDto>('/biblios', dto);
}

export async function updateBiblio(id: number, dto: BiblioDto): Promise<BiblioDto> {
  return put<BiblioDto>(`/biblios/${id}`, dto);
}

export async function deleteBiblio(id: number): Promise<void> {
  return del(`/biblios/${id}`);
}

export async function getBiblioItems(biblioId: number): Promise<ItemDto[]> {
  return get<ItemDto[]>(`/biblios/${biblioId}/items`);
}

// ── Patrons ───────────────────────────────────────────────────────────────────

export async function listPatrons(
  query?: string,
  page = 0,
  size = 50
): Promise<Page<PatronDto>> {
  const qs = new URLSearchParams();
  if (query) qs.set('q', query);
  qs.set('page', String(page));
  qs.set('size', String(size));
  return get<Page<PatronDto>>(`/patrons?${qs}`);
}

export async function getPatron(id: number): Promise<PatronDto> {
  return get<PatronDto>(`/patrons/${id}`);
}

export async function createPatron(dto: PatronDto): Promise<PatronDto> {
  return post<PatronDto>('/patrons', dto);
}

export async function updatePatron(id: number, dto: PatronDto): Promise<PatronDto> {
  return put<PatronDto>(`/patrons/${id}`, dto);
}

// ── Circulation ───────────────────────────────────────────────────────────────

export async function listCheckouts(
  page = 0,
  size = 50
): Promise<Page<CheckoutDto>> {
  const qs = new URLSearchParams({ page: String(page), size: String(size) });
  return get<Page<CheckoutDto>>(`/checkouts?${qs}`);
}

// ── Acquisitions ──────────────────────────────────────────────────────────────

export async function getAcquisitionsHome(
  patronId: number,
  branchCode: string
): Promise<AcquisitionsHomeDto> {
  const qs = new URLSearchParams({
    patron_id: String(patronId),
    branch_code: branchCode,
    only_my_library: 'false',
  });
  return get<AcquisitionsHomeDto>(`/acquisitions/home?${qs}`);
}
