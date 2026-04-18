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
import type { BiblioDto, Page } from '../types';
import { createBiblio, deleteBiblio } from '../api/client';

interface CatalogLoaderData {
  biblios: Page<BiblioDto>;
}

const COL_DEFS: ColDef<BiblioDto>[] = [
  { field: 'biblioId', headerName: 'ID', width: 80, sortable: true },
  { field: 'title', headerName: 'Title', flex: 2, sortable: true, filter: true },
  { field: 'author', headerName: 'Author', flex: 1, sortable: true, filter: true },
  { field: 'isbn', headerName: 'ISBN', width: 140 },
  { field: 'publisher', headerName: 'Publisher', flex: 1, filter: true },
  { field: 'copyrightdate', headerName: 'Year', width: 80 },
];

export default function CatalogPage() {
  const { biblios } = useLoaderData() as CatalogLoaderData;
  const revalidator = useRevalidator();
  const [showAdd, setShowAdd] = useState(false);
  const [selectedRow, setSelectedRow] = useState<BiblioDto | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { isSubmitting, errors },
  } = useForm<BiblioDto>();

  const onAddSubmit = async (data: BiblioDto) => {
    await createBiblio(data);
    reset();
    setShowAdd(false);
    revalidator.revalidate();
  };

  const onDelete = async () => {
    if (selectedRow?.biblioId == null) return;
    if (!confirm(`Delete "${selectedRow.title}"?`)) return;
    await deleteBiblio(selectedRow.biblioId);
    setSelectedRow(null);
    revalidator.revalidate();
  };

  return (
    <StackLayout gap={2} style={{ height: 'calc(100vh - 48px)' }}>
      <StackLayout direction="row" style={{ alignItems: 'center' }}>
        <Text styleAs="h2" style={{ flex: 1 }}>
          Catalog — Bibliographic Records
        </Text>
        <Text styleAs="label" style={{ marginRight: 8 }}>
          {biblios.totalElements} records
        </Text>
        <Button appearance="solid" sentiment="accented" onClick={() => setShowAdd(true)}>
          Add Biblio
        </Button>
        {selectedRow && (
          <Button
            appearance="solid"
            sentiment="negative"
            onClick={onDelete}
            style={{ marginLeft: 8 }}
          >
            Delete Selected
          </Button>
        )}
      </StackLayout>

      <div className="ag-theme-quartz" style={{ flex: 1, width: '100%' }}>
        <AgGridReact<BiblioDto>
          rowData={biblios.content}
          columnDefs={COL_DEFS}
          pagination
          paginationPageSize={50}
          rowSelection="single"
          onRowSelected={(e) => {
            if (e.node.isSelected()) setSelectedRow(e.data ?? null);
          }}
        />
      </div>

      {showAdd && (
        <Dialog open onOpenChange={(o) => !o && setShowAdd(false)} style={{ width: 480 }}>
          <DialogHeader header="Add Bibliographic Record" />
          <DialogContent>
            <form id="add-biblio-form" onSubmit={handleSubmit(onAddSubmit)}>
              <StackLayout gap={2}>
                <FormField validationStatus={errors.title ? 'error' : undefined}>
                  <FormFieldLabel>Title *</FormFieldLabel>
                  <Input
                    {...register('title', { required: 'Title is required' })}
                    placeholder="Enter title"
                  />
                </FormField>
                <FormField>
                  <FormFieldLabel>Author</FormFieldLabel>
                  <Input {...register('author')} placeholder="Enter author" />
                </FormField>
                <FormField>
                  <FormFieldLabel>ISBN</FormFieldLabel>
                  <Input {...register('isbn')} placeholder="Enter ISBN" />
                </FormField>
                <FormField>
                  <FormFieldLabel>Publisher</FormFieldLabel>
                  <Input {...register('publisher')} placeholder="Enter publisher" />
                </FormField>
                <FormField>
                  <FormFieldLabel>Copyright Year</FormFieldLabel>
                  <Input
                    {...register('copyrightdate', { valueAsNumber: true })}
                    placeholder="e.g. 2024"
                  />
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
              form="add-biblio-form"
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
