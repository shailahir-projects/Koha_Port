import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import {
  NavigationItem,
  StackLayout,
  Text,
  Button,
} from '@salt-ds/core';
import { useAuth } from '../components/useAuth';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/catalog', label: 'Catalog' },
  { to: '/patrons', label: 'Patrons' },
  { to: '/circulation', label: 'Circulation' },
  { to: '/acquisitions', label: 'Acquisitions' },
];

export default function AppLayout() {
  const { signOut } = useAuth();
  const navigate = useNavigate();

  const handleSignOut = () => {
    signOut();
    navigate('/login', { replace: true });
  };

  return (
    <StackLayout direction="row" style={{ minHeight: '100vh' }} gap={0}>
      {/* Sidebar */}
      <nav
        style={{
          width: 220,
          background: 'var(--salt-palette-neutral-background-strong)',
          borderRight: '1px solid var(--salt-palette-neutral-border)',
          display: 'flex',
          flexDirection: 'column',
          padding: 'var(--salt-spacing-200) 0',
        }}
      >
        <Text
          styleAs="h3"
          style={{
            padding: 'var(--salt-spacing-200) var(--salt-spacing-300)',
            borderBottom: '1px solid var(--salt-palette-neutral-border)',
            marginBottom: 'var(--salt-spacing-100)',
          }}
        >
          Koha ILS
        </Text>

        {NAV_ITEMS.map(({ to, label }) => (
          <NavLink key={to} to={to} style={{ textDecoration: 'none' }}>
            {({ isActive }) => (
              <NavigationItem active={isActive} orientation="vertical">
                {label}
              </NavigationItem>
            )}
          </NavLink>
        ))}

        <div style={{ flex: 1 }} />
        <div style={{ padding: 'var(--salt-spacing-200) var(--salt-spacing-300)' }}>
          <Button
            appearance="transparent"
            sentiment="neutral"
            onClick={handleSignOut}
            style={{ width: '100%' }}
          >
            Sign Out
          </Button>
        </div>
      </nav>

      {/* Main content */}
      <main style={{ flex: 1, overflow: 'auto', padding: 'var(--salt-spacing-300)' }}>
        <Outlet />
      </main>
    </StackLayout>
  );
}
