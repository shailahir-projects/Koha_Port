import { useState } from 'react';
import { useLoaderData, useRevalidator } from 'react-router-dom';
import { AgGridReact } from 'ag-grid-react';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-quartz.css';
import type { ColDef } from 'ag-grid-community';
import {
  Button,
  Dialog,
  DialogHeader,
  DialogContent,
  DialogActions,
  FormField,
  FormFieldLabel,
  Input,
  StackLayout,
  Text,
} from '@salt-ds/core';
import { useForm } from 'react-hook-form';
import type { PatronDto, Page } from '../types';
import { createPatron } from '../api/client';

interface PatronLoaderData {
  patrons: Page<PatronDto>;
}

const COL_DEFS: ColDef<PatronDto>[] = [
  { field: 'borrowernumber', headerName: 'ID', width: 80 },
  { field: 'cardnumber', headerName: 'Card No', width: 130 },
  { field: 'surname', headerName: 'Surname', flex: 1, sortable: true, filter: true },
  { field: 'firstname', headerName: 'First Name', flex: 1, sortable: true, filter: true },
  { field: 'email', headerName: 'Email', flex: 1 },
  { field: 'branchcode', headerName: 'Branch', width: 100 },
  { field: 'categorycode', headerName: 'Category', width: 110 },
  { field: 'dateexpiry', headerName: 'Expiry', width: 120 },
];

export default function PatronsPage() {
  const { patrons } = useLoaderData() as PatronLoaderData;
  const revalidator = useRevalidator();
  const [showAdd, setShowAdd] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { isSubmitting, errors },
  } = useForm<PatronDto>();

  const onAddSubmit = async (data: PatronDto) => {
    await createPatron(data);
    reset();
    setShowAdd(false);
    revalidator.revalidate();
  };

  return (
    <StackLayout gap={2} style={{ height: 'calc(100vh - 48px)' }}>
      <StackLayout direction="row" style={{ alignItems: 'center' }}>
        <Text styleAs="h2" style={{ flex: 1 }}>
          Patron Management
        </Text>
        <Text styleAs="label" style={{ marginRight: 8 }}>
          {patrons.totalElements} patrons
        </Text>
        <Button appearance="solid" sentiment="accented" onClick={() => setShowAdd(true)}>
          Add Patron
        </Button>
      </StackLayout>

      <div className="ag-theme-quartz" style={{ flex: 1, width: '100%' }}>
        <AgGridReact<PatronDto>
          rowData={patrons.content}
          columnDefs={COL_DEFS}
          pagination
          paginationPageSize={50}
          rowSelection="single"
        />
      </div>

      {showAdd && (
        <Dialog open onOpenChange={(o) => !o && setShowAdd(false)} style={{ width: 520 }}>
          <DialogHeader header="Add Patron" />
          <DialogContent>
            <form id="add-patron-form" onSubmit={handleSubmit(onAddSubmit)}>
              <StackLayout gap={2}>
                <FormField validationStatus={errors.surname ? 'error' : undefined}>
                  <FormFieldLabel>Surname *</FormFieldLabel>
                  <Input
                    {...register('surname', { required: 'Surname is required' })}
                    placeholder="Enter surname"
                  />
                </FormField>
                <FormField>
                  <FormFieldLabel>First Name</FormFieldLabel>
                  <Input {...register('firstname')} placeholder="Enter first name" />
                </FormField>
                <FormField>
                  <FormFieldLabel>Card Number</FormFieldLabel>
                  <Input {...register('cardnumber')} placeholder="Enter card number" />
                </FormField>
                <FormField>
                  <FormFieldLabel>Email</FormFieldLabel>
                  <Input {...register('email')} inputProps={{ type: 'email' }} placeholder="Enter email" />
                </FormField>
                <FormField>
                  <FormFieldLabel>Branch Code</FormFieldLabel>
                  <Input {...register('branchcode')} placeholder="e.g. CPL" />
                </FormField>
                <FormField>
                  <FormFieldLabel>Category Code</FormFieldLabel>
                  <Input {...register('categorycode')} placeholder="e.g. PT" />
                </FormField>
                <FormField>
                  <FormFieldLabel>User ID</FormFieldLabel>
                  <Input {...register('userid')} placeholder="Enter user ID" />
                </FormField>
              </StackLayout>
            </form>
          </DialogContent>
          <DialogActions>
            <Button appearance="transparent" onClick={() => setShowAdd(false)}>
              Cancel
            </Button>
            <Button
              type="submit"
              form="add-patron-form"
              appearance="solid"
              sentiment="accented"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Saving…' : 'Save'}
            </Button>
          </DialogActions>
        </Dialog>
      )}
    </StackLayout>
  );
}
