'use client';

import { useEffect, useState } from 'react';
import AppLayout from '@/components/AppLayout';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { api } from '@/lib/api';
import { useAuth } from '@/lib/auth';
import { Plus } from 'lucide-react';

interface Contract {
  id: string;
  machineId: string;
  machineName: string;
  vendorId: string;
  vendorName: string;
  startDate: string;
  endDate: string;
  ratePerHour: number;
  currency: string;
  status: string;
  createdAt: string;
}

export default function ContractsPage() {
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const { isAdmin } = useAuth();
  const [form, setForm] = useState({ machineId: '', vendorId: '', startDate: '', endDate: '', ratePerHour: '', currency: 'ZAR' });

  useEffect(() => { loadContracts(); }, []);

  const loadContracts = () => {
    api.get('/api/contracts').then(setContracts).catch(console.error).finally(() => setLoading(false));
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/contracts', { ...form, ratePerHour: parseFloat(form.ratePerHour) });
      setShowForm(false);
      setForm({ machineId: '', vendorId: '', startDate: '', endDate: '', ratePerHour: '', currency: 'ZAR' });
      loadContracts();
    } catch (err: any) { alert(err.message); }
  };

  const statusVariant = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success' as const;
      case 'EXPIRED': return 'secondary' as const;
      case 'TERMINATED': return 'danger' as const;
      default: return 'secondary' as const;
    }
  };

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-primary">Contracts</h1>
          <p className="text-sm text-text-secondary mt-1">{contracts.length} rental contracts</p>
        </div>
        {isAdmin && (
          <Button onClick={() => setShowForm(!showForm)}>
            <Plus className="h-4 w-4 mr-2" /> New Contract
          </Button>
        )}
      </div>

      {showForm && (
        <Card className="mb-6">
          <CardHeader><CardTitle>Create New Contract</CardTitle></CardHeader>
          <CardContent>
            <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input placeholder="Machine ID" value={form.machineId} onChange={e => setForm({...form, machineId: e.target.value})} required />
              <Input placeholder="Vendor ID" value={form.vendorId} onChange={e => setForm({...form, vendorId: e.target.value})} required />
              <Input type="date" placeholder="Start Date" value={form.startDate} onChange={e => setForm({...form, startDate: e.target.value})} required />
              <Input type="date" placeholder="End Date" value={form.endDate} onChange={e => setForm({...form, endDate: e.target.value})} />
              <Input type="number" step="0.01" placeholder="Rate per Hour (ZAR)" value={form.ratePerHour} onChange={e => setForm({...form, ratePerHour: e.target.value})} required />
              <div className="flex gap-2">
                <Button type="submit">Create</Button>
                <Button type="button" variant="outline" onClick={() => setShowForm(false)}>Cancel</Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Machine</TableHead>
              <TableHead>Vendor</TableHead>
              <TableHead>Start Date</TableHead>
              <TableHead>End Date</TableHead>
              <TableHead>Rate/Hour</TableHead>
              <TableHead>Status</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8">Loading...</TableCell></TableRow>
            ) : contracts.length === 0 ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8 text-text-secondary">No contracts found</TableCell></TableRow>
            ) : (
              contracts.map(contract => (
                <TableRow key={contract.id}>
                  <TableCell className="font-medium">{contract.machineName || contract.machineId}</TableCell>
                  <TableCell>{contract.vendorName || contract.vendorId}</TableCell>
                  <TableCell>{contract.startDate}</TableCell>
                  <TableCell>{contract.endDate || '-'}</TableCell>
                  <TableCell>R {contract.ratePerHour?.toFixed(2)}</TableCell>
                  <TableCell><Badge variant={statusVariant(contract.status)}>{contract.status}</Badge></TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </Card>
    </AppLayout>
  );
}
