import { useLoaderData } from 'react-router-dom';
import { AgGridReact } from 'ag-grid-react';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-quartz.css';
import type { ColDef, ValueFormatterParams } from 'ag-grid-community';
import { Card, StackLayout, Text } from '@salt-ds/core';
import type { AcquisitionsHomeDto, BudgetDto } from '../types';

interface AcquisitionsLoaderData {
  home: AcquisitionsHomeDto | null;
}

const BUDGET_COL_DEFS: ColDef<BudgetDto>[] = [
  {
    field: 'budgetDisplayName',
    headerName: 'Fund',
    flex: 2,
    filter: true,
    cellStyle: (params) => ({
      paddingLeft: `${((params.data?.depth ?? 0) * 16) + 8}px`,
    }),
  },
  {
    field: 'budgetAmount',
    headerName: 'Amount',
    width: 130,
    sortable: true,
    valueFormatter: (p: ValueFormatterParams) => formatNum(p.value),
  },
  {
    field: 'budgetSpent',
    headerName: 'Spent',
    width: 130,
    sortable: true,
    valueFormatter: (p: ValueFormatterParams) => formatNum(p.value),
  },
  {
    field: 'budgetOrdered',
    headerName: 'Ordered',
    width: 130,
    valueFormatter: (p: ValueFormatterParams) => formatNum(p.value),
  },
  {
    field: 'budgetAvail',
    headerName: 'Available',
    width: 130,
    sortable: true,
    valueFormatter: (p: ValueFormatterParams) => formatNum(p.value),
    cellStyle: (params) => ({
      color:
        params.value != null && (params.value as number) < 0
          ? 'var(--salt-status-negative-foreground)'
          : 'inherit',
    }),
  },
  {
    field: 'budgetPeriodActive',
    headerName: 'Active',
    width: 90,
    valueFormatter: (p: ValueFormatterParams) => (p.value ? 'Yes' : 'No'),
  },
];

export default function AcquisitionsPage() {
  const { home } = useLoaderData() as AcquisitionsLoaderData;

  const summaryStats = [
    { label: 'Pending Suggestions', value: home?.suggestionsCount ?? '—' },
    { label: 'Total Budget', value: formatNum(home?.total) },
    { label: 'Spent', value: formatNum(home?.totalSpent) },
    { label: 'Ordered', value: formatNum(home?.totalOrdered) },
    { label: 'Available', value: formatNum(home?.totalAvailable) },
    { label: 'Currency', value: home?.activeCurrency ?? '—' },
  ];

  return (
    <StackLayout gap={3} style={{ height: 'calc(100vh - 48px)' }}>
      <Text styleAs="h2">Acquisitions</Text>

      {/* Summary cards */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(6, 1fr)',
          gap: 'var(--salt-spacing-100)',
        }}
      >
        {summaryStats.map(({ label, value }) => (
          <Card key={label}>
            <StackLayout gap={1}>
              <Text styleAs="label">{label}</Text>
              <Text styleAs="h4">{String(value)}</Text>
            </StackLayout>
          </Card>
        ))}
      </div>

      {/* Budget hierarchy grid */}
      <Text styleAs="h3">Fund Hierarchy</Text>
      <div className="ag-theme-quartz" style={{ flex: 1, width: '100%' }}>
        <AgGridReact<BudgetDto>
          rowData={home?.budgets ?? []}
          columnDefs={BUDGET_COL_DEFS}
          pagination
          paginationPageSize={50}
          rowSelection="single"
        />
      </div>
    </StackLayout>
  );
}

function formatNum(value: number | undefined): string {
  if (value == null) return '—';
  return Number(value).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}
