import { Card, GridLayout, GridItem, Text, StackLayout } from '@salt-ds/core';
import { useLoaderData } from 'react-router-dom';
import type { AcquisitionsHomeDto } from '../types';

interface DashboardLoaderData {
  acquisitions: AcquisitionsHomeDto | null;
}

export default function DashboardPage() {
  const data = useLoaderData() as DashboardLoaderData;
  const acq = data?.acquisitions;

  const stats = [
    { label: 'Pending Suggestions', value: acq?.suggestionsCount ?? '—' },
    { label: 'Total Budget', value: formatCurrency(acq?.total, acq?.activeCurrency) },
    { label: 'Spent', value: formatCurrency(acq?.totalSpent, acq?.activeCurrency) },
    { label: 'Ordered', value: formatCurrency(acq?.totalOrdered, acq?.activeCurrency) },
    { label: 'Available', value: formatCurrency(acq?.totalAvailable, acq?.activeCurrency) },
    { label: 'Budget Periods', value: acq?.budgetPeriods?.length ?? '—' },
  ];

  return (
    <StackLayout gap={3}>
      <Text styleAs="h2">Dashboard</Text>

      <GridLayout columns={3} gap={2}>
        {stats.map(({ label, value }) => (
          <GridItem key={label}>
            <Card style={{ height: '100%' }}>
              <StackLayout gap={1}>
                <Text styleAs="label">{label}</Text>
                <Text styleAs="h3">{String(value)}</Text>
              </StackLayout>
            </Card>
          </GridItem>
        ))}
      </GridLayout>

      {acq?.budgets && acq.budgets.length > 0 && (
        <StackLayout gap={2}>
          <Text styleAs="h3">Budget Summary</Text>
          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr 1fr', gap: 8 }}>
            <Text style={{ fontWeight: 700 }}>Fund</Text>
            <Text style={{ fontWeight: 700 }}>Amount</Text>
            <Text style={{ fontWeight: 700 }}>Spent</Text>
            <Text style={{ fontWeight: 700 }}>Available</Text>
            {acq.budgets.slice(0, 10).map((b, i) => (
              <>
                <Text key={`name-${i}`}>{b.budgetDisplayName ?? b.budgetName}</Text>
                <Text key={`amt-${i}`}>{formatCurrency(b.budgetAmount, acq.activeCurrency)}</Text>
                <Text key={`spent-${i}`}>{formatCurrency(b.budgetSpent, acq.activeCurrency)}</Text>
                <Text key={`avail-${i}`}>{formatCurrency(b.budgetAvail, acq.activeCurrency)}</Text>
              </>
            ))}
          </div>
        </StackLayout>
      )}
    </StackLayout>
  );
}

function formatCurrency(value: number | undefined, currency?: string): string {
  if (value == null) return '—';
  const sym = currency ?? '';
  return `${sym}${Number(value).toFixed(2)}`;
}
