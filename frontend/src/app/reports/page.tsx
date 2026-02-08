'use client';

import { useState } from 'react';
import AppLayout from '@/components/AppLayout';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { api } from '@/lib/api';
import { useAuth } from '@/lib/auth';
import { BarChart3, Download, RefreshCw } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

interface UsageReport {
  id: string;
  contractId: string;
  machineId: string;
  machineName: string;
  vendorId: string;
  vendorName: string;
  periodStart: string;
  periodEnd: string;
  totalHours: number;
  billableAmount: number;
  currency: string;
  generatedAt: string;
}

export default function ReportsPage() {
  const [reports, setReports] = useState<UsageReport[]>([]);
  const [loading, setLoading] = useState(false);
  const { isAdmin } = useAuth();
  const today = new Date().toISOString().split('T')[0];
  const monthAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
  const [startDate, setStartDate] = useState(monthAgo);
  const [endDate, setEndDate] = useState(today);

  const loadReports = async () => {
    setLoading(true);
    try {
      const data = await api.get(`/api/reports?startDate=${startDate}&endDate=${endDate}`);
      setReports(data);
    } catch (err: any) { alert(err.message); }
    finally { setLoading(false); }
  };

  const generateMonthly = async () => {
    const date = new Date(startDate);
    try {
      await api.post(`/api/reports/generate?year=${date.getFullYear()}&month=${date.getMonth() + 1}`, {});
      loadReports();
    } catch (err: any) { alert(err.message); }
  };

  const exportExcel = () => {
    const token = localStorage.getItem('accessToken');
    const apiUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    window.open(`${apiUrl}/api/reports/export/excel?startDate=${startDate}&endDate=${endDate}&token=${token}`);
  };

  const chartData = reports.reduce((acc: any[], r) => {
    const key = r.machineName || r.machineId;
    const existing = acc.find(a => a.name === key);
    if (existing) {
      existing.hours += r.totalHours;
      existing.amount += r.billableAmount;
    } else {
      acc.push({ name: key, hours: r.totalHours, amount: r.billableAmount });
    }
    return acc;
  }, []);

  const totalHours = reports.reduce((sum, r) => sum + (r.totalHours || 0), 0);
  const totalAmount = reports.reduce((sum, r) => sum + (r.billableAmount || 0), 0);

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-primary">Usage Reports</h1>
          <p className="text-sm text-text-secondary mt-1">View and generate usage reports</p>
        </div>
        <div className="flex gap-2">
          {isAdmin && (
            <Button variant="outline" onClick={generateMonthly}>
              <RefreshCw className="h-4 w-4 mr-2" /> Generate Monthly
            </Button>
          )}
          <Button variant="outline" onClick={exportExcel}>
            <Download className="h-4 w-4 mr-2" /> Export Excel
          </Button>
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
            <Button onClick={loadReports} disabled={loading}>
              <BarChart3 className="h-4 w-4 mr-2" /> {loading ? 'Loading...' : 'Load Reports'}
            </Button>
          </div>
        </CardContent>
      </Card>

      {reports.length > 0 && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            <Card>
              <CardHeader className="pb-2"><CardTitle className="text-sm text-text-secondary">Total Hours</CardTitle></CardHeader>
              <CardContent><div className="text-2xl font-bold">{totalHours.toFixed(1)}</div></CardContent>
            </Card>
            <Card>
              <CardHeader className="pb-2"><CardTitle className="text-sm text-text-secondary">Total Billable</CardTitle></CardHeader>
              <CardContent><div className="text-2xl font-bold">R {totalAmount.toLocaleString()}</div></CardContent>
            </Card>
          </div>

          {chartData.length > 0 && (
            <Card className="mb-6">
              <CardHeader><CardTitle>Hours by Machine</CardTitle></CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
                    <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                    <YAxis tick={{ fontSize: 12 }} />
                    <Tooltip />
                    <Bar dataKey="hours" fill="#0969DA" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          )}
        </>
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
              <TableRow><TableCell colSpan={6} className="text-center py-8 text-text-secondary">Select a date range and click Load Reports</TableCell></TableRow>
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
