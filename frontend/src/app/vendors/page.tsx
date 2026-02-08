'use client';

import { useEffect, useState } from 'react';
import AppLayout from '@/components/AppLayout';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { api } from '@/lib/api';
import { useAuth } from '@/lib/auth';
import { Plus, Search } from 'lucide-react';

interface Vendor {
  id: string;
  companyName: string;
  contactName: string;
  email: string;
  phone: string;
  address: string;
  createdAt: string;
}

export default function VendorsPage() {
  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);
  const { isAdmin } = useAuth();
  const [form, setForm] = useState({ companyName: '', contactName: '', email: '', phone: '', address: '' });

  useEffect(() => { loadVendors(); }, []);

  const loadVendors = () => {
    api.get('/api/vendors').then(setVendors).catch(console.error).finally(() => setLoading(false));
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/vendors', form);
      setShowForm(false);
      setForm({ companyName: '', contactName: '', email: '', phone: '', address: '' });
      loadVendors();
    } catch (err: any) { alert(err.message); }
  };

  const filtered = vendors.filter(v =>
    v.companyName.toLowerCase().includes(search.toLowerCase()) ||
    v.contactName?.toLowerCase().includes(search.toLowerCase()) ||
    v.email?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-primary">Vendors</h1>
          <p className="text-sm text-text-secondary mt-1">{vendors.length} vendors registered</p>
        </div>
        {isAdmin && (
          <Button onClick={() => setShowForm(!showForm)}>
            <Plus className="h-4 w-4 mr-2" /> Add Vendor
          </Button>
        )}
      </div>

      {showForm && (
        <Card className="mb-6">
          <CardHeader><CardTitle>Add New Vendor</CardTitle></CardHeader>
          <CardContent>
            <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input placeholder="Company Name" value={form.companyName} onChange={e => setForm({...form, companyName: e.target.value})} required />
              <Input placeholder="Contact Name" value={form.contactName} onChange={e => setForm({...form, contactName: e.target.value})} />
              <Input placeholder="Email" type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} />
              <Input placeholder="Phone" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} />
              <Input placeholder="Address" value={form.address} onChange={e => setForm({...form, address: e.target.value})} />
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
          <Input placeholder="Search vendors..." value={search} onChange={e => setSearch(e.target.value)} className="pl-9" />
        </div>
      </div>

      <Card>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Company</TableHead>
              <TableHead>Contact</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Phone</TableHead>
              <TableHead>Address</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={5} className="text-center py-8">Loading...</TableCell></TableRow>
            ) : filtered.length === 0 ? (
              <TableRow><TableCell colSpan={5} className="text-center py-8 text-text-secondary">No vendors found</TableCell></TableRow>
            ) : (
              filtered.map(vendor => (
                <TableRow key={vendor.id}>
                  <TableCell className="font-medium">{vendor.companyName}</TableCell>
                  <TableCell>{vendor.contactName}</TableCell>
                  <TableCell>{vendor.email}</TableCell>
                  <TableCell>{vendor.phone}</TableCell>
                  <TableCell className="max-w-xs truncate">{vendor.address}</TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </Card>
    </AppLayout>
  );
}
