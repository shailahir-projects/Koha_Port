import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider } from 'react-router-dom'
import { SaltProvider } from '@salt-ds/core'
import '@salt-ds/core/css/salt-core.css'
import './index.css'
import { router } from './routes/router.tsx'
import { AuthProvider } from './components/AuthContext.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <SaltProvider>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </SaltProvider>
  </StrictMode>,
)
