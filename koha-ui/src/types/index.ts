// ── Auth ──────────────────────────────────────────────────────────────────────
export interface LoginRequest {
  username: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

// ── Catalog ───────────────────────────────────────────────────────────────────
export interface BiblioDto {
  biblioId?: number;
  title?: string;
  author?: string;
  isbn?: string;
  frameworkCode?: string;
  copyrightdate?: number;
  publicationyear?: string;
  publisher?: string;
  notes?: string;
}

export interface ItemDto {
  itemId?: number;
  biblioId?: number;
  barcode?: string;
  homebranch?: string;
  holdingbranch?: string;
  itemcallnumber?: string;
  itype?: string;
  location?: string;
  notforloan?: number;
  damaged?: number;
  lost?: number;
  withdrawn?: number;
}

// ── Patron ────────────────────────────────────────────────────────────────────
export interface PatronDto {
  borrowernumber?: number;
  cardnumber?: string;
  surname?: string;
  firstname?: string;
  email?: string;
  phone?: string;
  mobile?: string;
  branchcode?: string;
  categorycode?: string;
  dateenrolled?: string;
  dateexpiry?: string;
  userid?: string;
  address?: string;
  city?: string;
  country?: string;
}

// ── Circulation ───────────────────────────────────────────────────────────────
export interface CheckoutDto {
  checkoutId?: number;
  borrowernumber?: number;
  itemnumber?: number;
  issuedate?: string;
  date_due?: string;
  returndate?: string;
  barcode?: string;
  title?: string;
}

// ── Acquisitions ──────────────────────────────────────────────────────────────
export interface BudgetDto {
  budgetId?: number;
  budgetName?: string;
  budgetDisplayName?: string;
  budgetAmount?: number;
  budgetSpent?: number;
  budgetOrdered?: number;
  budgetAvail?: number;
  budgetPeriodActive?: boolean;
  depth?: number;
}

export interface BudgetPeriodDto {
  budgetPeriodId?: number;
  budgetPeriodDescription?: string;
  budgetPeriodStartDate?: string;
  budgetPeriodEndDate?: string;
  budgetPeriodActive?: boolean;
  hierarchy?: BudgetDto[];
}

export interface AcquisitionsHomeDto {
  suggestionsCount?: number;
  total?: number;
  totalSpent?: number;
  totalOrdered?: number;
  totalAvailable?: number;
  activeCurrency?: string;
  budgets?: BudgetDto[];
  budgetPeriods?: BudgetPeriodDto[];
}

// ── Pagination ────────────────────────────────────────────────────────────────
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
