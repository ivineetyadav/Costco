'use client';

import { useState } from 'react';
import AppLayout from '@/components/AppLayout';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { api } from '@/lib/api';
import { Receipt, Download } from 'lucide-react';

interface BillingReport {
  id: string;
  machineName: string;
  machineId: string;
  vendorName: string;
  vendorId: string;
  periodStart: string;
  periodEnd: string;
  totalHours: number;
  billableAmount: number;
  currency: string;
}

export default function BillingPage() {
  const [reports, setReports] = useState<BillingReport[]>([]);
  const [loading, setLoading] = useState(false);
  const today = new Date().toISOString().split('T')[0];
  const monthAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
  const [startDate, setStartDate] = useState(monthAgo);
  const [endDate, setEndDate] = useState(today);

  const loadBilling = async () => {
    setLoading(true);
    try {
      const data = await api.get(`/api/billing?startDate=${startDate}&endDate=${endDate}`);
      setReports(data);
    } catch (err: any) { alert(err.message); }
    finally { setLoading(false); }
  };

  const totalAmount = reports.reduce((sum, r) => sum + (r.billableAmount || 0), 0);

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-primary">Billing</h1>
          <p className="text-sm text-text-secondary mt-1">Invoices and billing records</p>
        </div>
      </div>

      <Card className="mb-6">
        <CardContent className="pt-6">
          <div className="flex items-end gap-4">
            <div>
              <label className="text-sm font-medium text-text-primary mb-1 block">Start Date</label>
              <Input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} />
            </div>
            <div>
              <label className="text-sm font-medium text-text-primary mb-1 block">End Date</label>
              <Input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} />
            </div>
            <Button onClick={loadBilling} disabled={loading}>
              <Receipt className="h-4 w-4 mr-2" /> {loading ? 'Loading...' : 'Load Billing'}
            </Button>
          </div>
        </CardContent>
      </Card>

      {reports.length > 0 && (
        <Card className="mb-6">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-text-secondary">Total Outstanding</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-text-primary">R {totalAmount.toLocaleString()}</div>
          </CardContent>
        </Card>
      )}

      <Card>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Machine</TableHead>
              <TableHead>Vendor</TableHead>
              <TableHead>Period</TableHead>
              <TableHead>Hours</TableHead>
              <TableHead>Amount</TableHead>
              <TableHead>Currency</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {reports.length === 0 ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8 text-text-secondary">Select a date range to view billing</TableCell></TableRow>
            ) : (
              reports.map(report => (
                <TableRow key={report.id}>
                  <TableCell className="font-medium">{report.machineName || report.machineId}</TableCell>
                  <TableCell>{report.vendorName || report.vendorId}</TableCell>
                  <TableCell className="text-sm">{report.periodStart} to {report.periodEnd}</TableCell>
                  <TableCell>{report.totalHours?.toFixed(1)}</TableCell>
                  <TableCell className="font-medium">R {report.billableAmount?.toLocaleString()}</TableCell>
                  <TableCell>{report.currency}</TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </Card>
    </AppLayout>
  );
}
