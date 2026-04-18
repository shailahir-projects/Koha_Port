import { useLoaderData } from 'react-router-dom';
import { AgGridReact } from 'ag-grid-react';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-quartz.css';
import type { ColDef } from 'ag-grid-community';
import { StackLayout, Text } from '@salt-ds/core';
import type { CheckoutDto, Page } from '../types';

interface CirculationLoaderData {
  checkouts: Page<CheckoutDto>;
}

const COL_DEFS: ColDef<CheckoutDto>[] = [
  { field: 'checkoutId', headerName: 'ID', width: 80 },
  { field: 'borrowernumber', headerName: 'Patron', width: 100 },
  { field: 'barcode', headerName: 'Barcode', width: 140 },
  { field: 'title', headerName: 'Title', flex: 2, filter: true },
  { field: 'issuedate', headerName: 'Issued', width: 130 },
  { field: 'date_due', headerName: 'Due Date', width: 130, sortable: true },
  { field: 'returndate', headerName: 'Returned', width: 130 },
];

export default function CirculationPage() {
  const { checkouts } = useLoaderData() as CirculationLoaderData;

  return (
    <StackLayout gap={2} style={{ height: 'calc(100vh - 48px)' }}>
      <StackLayout direction="row" style={{ alignItems: 'center' }}>
        <Text styleAs="h2" style={{ flex: 1 }}>
          Circulation — Checkouts
        </Text>
        <Text styleAs="label">{checkouts.totalElements} records</Text>
      </StackLayout>

      <div className="ag-theme-quartz" style={{ flex: 1, width: '100%' }}>
        <AgGridReact<CheckoutDto>
          rowData={checkouts.content}
          columnDefs={COL_DEFS}
          pagination
          paginationPageSize={50}
          rowSelection="single"
        />
      </div>
    </StackLayout>
  );
}
