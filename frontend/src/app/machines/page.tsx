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
import { Plus, Search } from 'lucide-react';

interface Machine {
  id: string;
  name: string;
  type: string;
  model: string;
  serialNumber: string;
  powerRating: string;
  status: string;
  createdAt: string;
}

export default function MachinesPage() {
  const [machines, setMachines] = useState<Machine[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);
  const { isAdmin } = useAuth();
  const [form, setForm] = useState({ name: '', type: '', model: '', serialNumber: '', powerRating: '' });

  useEffect(() => {
    loadMachines();
  }, []);

  const loadMachines = () => {
    api.get('/api/machines')
      .then(setMachines)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/machines', form);
      setShowForm(false);
      setForm({ name: '', type: '', model: '', serialNumber: '', powerRating: '' });
      loadMachines();
    } catch (err: any) {
      alert(err.message);
    }
  };

  const filtered = machines.filter(m =>
    m.name.toLowerCase().includes(search.toLowerCase()) ||
    m.type?.toLowerCase().includes(search.toLowerCase()) ||
    m.serialNumber?.toLowerCase().includes(search.toLowerCase())
  );

  const statusVariant = (status: string) => {
    switch (status) {
      case 'AVAILABLE': return 'success' as const;
      case 'RENTED': return 'default' as const;
      case 'MAINTENANCE': return 'warning' as const;
      default: return 'secondary' as const;
    }
  };

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-primary">Machines</h1>
          <p className="text-sm text-text-secondary mt-1">{machines.length} machines registered</p>
        </div>
        {isAdmin && (
          <Button onClick={() => setShowForm(!showForm)}>
            <Plus className="h-4 w-4 mr-2" /> Add Machine
          </Button>
        )}
      </div>

      {showForm && (
        <Card className="mb-6">
          <CardHeader><CardTitle>Add New Machine</CardTitle></CardHeader>
          <CardContent>
            <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input placeholder="Machine Name" value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
              <Input placeholder="Type (e.g. SCRAPER_WINCH)" value={form.type} onChange={e => setForm({...form, type: e.target.value})} />
              <Input placeholder="Model" value={form.model} onChange={e => setForm({...form, model: e.target.value})} />
              <Input placeholder="Serial Number" value={form.serialNumber} onChange={e => setForm({...form, serialNumber: e.target.value})} />
              <Input placeholder="Power Rating (e.g. 75KW)" value={form.powerRating} onChange={e => setForm({...form, powerRating: e.target.value})} />
              <div className="flex gap-2">
                <Button type="submit">Save</Button>
                <Button type="button" variant="outline" onClick={() => setShowForm(false)}>Cancel</Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <div className="mb-4">
        <div className="relative w-72">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-text-secondary" />
          <Input placeholder="Search machines..." value={search} onChange={e => setSearch(e.target.value)} className="pl-9" />
        </div>
      </div>

      <Card>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Type</TableHead>
              <TableHead>Model</TableHead>
              <TableHead>Serial Number</TableHead>
              <TableHead>Power</TableHead>
              <TableHead>Status</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8">Loading...</TableCell></TableRow>
            ) : filtered.length === 0 ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8 text-text-secondary">No machines found</TableCell></TableRow>
            ) : (
              filtered.map(machine => (
                <TableRow key={machine.id}>
                  <TableCell className="font-medium">{machine.name}</TableCell>
                  <TableCell>{machine.type}</TableCell>
                  <TableCell>{machine.model}</TableCell>
                  <TableCell className="font-mono text-xs">{machine.serialNumber}</TableCell>
                  <TableCell>{machine.powerRating}</TableCell>
                  <TableCell><Badge variant={statusVariant(machine.status)}>{machine.status}</Badge></TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </Card>
    </AppLayout>
  );
}
