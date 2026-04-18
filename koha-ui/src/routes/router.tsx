import { createBrowserRouter, redirect } from 'react-router-dom';
import { getToken } from '../api/client';
import {
  listBiblios,
  listPatrons,
  listCheckouts,
  getAcquisitionsHome,
} from '../api/client';

import LoginPage from '../pages/LoginPage';
import AppLayout from '../pages/AppLayout';
import DashboardPage from '../pages/DashboardPage';
import CatalogPage from '../pages/CatalogPage';
import PatronsPage from '../pages/PatronsPage';
import CirculationPage from '../pages/CirculationPage';
import AcquisitionsPage from '../pages/AcquisitionsPage';

// ── Guards ────────────────────────────────────────────────────────────────────

function requireAuth() {
  if (!getToken()) {
    return redirect('/login');
  }
  return null;
}

function requireGuest() {
  if (getToken()) {
    return redirect('/dashboard');
  }
  return null;
}

// ── Loaders ───────────────────────────────────────────────────────────────────

async function dashboardLoader() {
  const guard = requireAuth();
  if (guard) return guard;
  try {
    const acquisitions = await getAcquisitionsHome(1, 'CPL');
    return { acquisitions };
  } catch {
    return { acquisitions: null };
  }
}

async function catalogLoader() {
  const guard = requireAuth();
  if (guard) return guard;
  try {
    const biblios = await listBiblios(undefined, 0, 50);
    return { biblios };
  } catch {
    return { biblios: { content: [], totalElements: 0, totalPages: 0, size: 50, number: 0 } };
  }
}

async function patronsLoader() {
  const guard = requireAuth();
  if (guard) return guard;
  try {
    const patrons = await listPatrons(undefined, 0, 50);
    return { patrons };
  } catch {
    return { patrons: { content: [], totalElements: 0, totalPages: 0, size: 50, number: 0 } };
  }
}

async function circulationLoader() {
  const guard = requireAuth();
  if (guard) return guard;
  try {
    const checkouts = await listCheckouts(0, 50);
    return { checkouts };
  } catch {
    return { checkouts: { content: [], totalElements: 0, totalPages: 0, size: 50, number: 0 } };
  }
}

async function acquisitionsLoader() {
  const guard = requireAuth();
  if (guard) return guard;
  try {
    const home = await getAcquisitionsHome(1, 'CPL');
    return { home };
  } catch {
    return { home: null };
  }
}

// ── Router ────────────────────────────────────────────────────────────────────

export const router = createBrowserRouter([
  {
    path: '/login',
    loader: requireGuest,
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <AppLayout />,
    children: [
      {
        index: true,
        loader: () => redirect('/dashboard'),
      },
      {
        path: 'dashboard',
        loader: dashboardLoader,
        element: <DashboardPage />,
      },
      {
        path: 'catalog',
        loader: catalogLoader,
        element: <CatalogPage />,
      },
      {
        path: 'patrons',
        loader: patronsLoader,
        element: <PatronsPage />,
      },
      {
        path: 'circulation',
        loader: circulationLoader,
        element: <CirculationPage />,
      },
      {
        path: 'acquisitions',
        loader: acquisitionsLoader,
        element: <AcquisitionsPage />,
      },
    ],
  },
  {
    path: '*',
    loader: () => redirect('/dashboard'),
  },
]);
