'use client';

import { useEffect, useState } from 'react';
import AppLayout from '@/components/AppLayout';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { api } from '@/lib/api';
import { Plus, UserPlus } from 'lucide-react';

interface User {
  id: string;
  email: string;
  fullName: string;
  role: string;
  vendorId: string;
  isActive: boolean;
  lastLoginAt: string;
  createdAt: string;
}

export default function SettingsPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ email: '', fullName: '', password: '', role: 'MANAGER', vendorId: '' });

  useEffect(() => { loadUsers(); }, []);

  const loadUsers = () => {
    api.get('/api/admin/users').then(setUsers).catch(console.error).finally(() => setLoading(false));
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/admin/users', {
        ...form,
        vendorId: form.vendorId || null,
      });
      setShowForm(false);
      setForm({ email: '', fullName: '', password: '', role: 'MANAGER', vendorId: '' });
      loadUsers();
    } catch (err: any) { alert(err.message); }
  };

  const toggleActive = async (user: User) => {
    try {
      await api.put(`/api/admin/users/${user.id}`, { isActive: !user.isActive });
      loadUsers();
    } catch (err: any) { alert(err.message); }
  };

  const roleVariant = (role: string) => {
    switch (role) {
      case 'ADMIN': return 'danger' as const;
      case 'MANAGER': return 'default' as const;
      case 'VENDOR': return 'success' as const;
      default: return 'secondary' as const;
    }
  };

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-primary">Settings</h1>
          <p className="text-sm text-text-secondary mt-1">User management and system settings</p>
        </div>
        <Button onClick={() => setShowForm(!showForm)}>
          <UserPlus className="h-4 w-4 mr-2" /> Add User
        </Button>
      </div>

      {showForm && (
        <Card className="mb-6">
          <CardHeader><CardTitle>Create New User</CardTitle></CardHeader>
          <CardContent>
            <form onSubmit={handleCreate} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input placeholder="Email" type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} required />
              <Input placeholder="Full Name" value={form.fullName} onChange={e => setForm({...form, fullName: e.target.value})} required />
              <Input placeholder="Password" type="password" value={form.password} onChange={e => setForm({...form, password: e.target.value})} required />
              <select
                className="flex h-9 w-full rounded-md border border-border bg-white px-3 py-1 text-sm"
                value={form.role}
                onChange={e => setForm({...form, role: e.target.value})}
              >
                <option value="ADMIN">Admin</option>
                <option value="MANAGER">Manager</option>
                <option value="VENDOR">Vendor</option>
              </select>
              {form.role === 'VENDOR' && (
                <Input placeholder="Vendor ID" value={form.vendorId} onChange={e => setForm({...form, vendorId: e.target.value})} />
              )}
              <div className="flex gap-2">
                <Button type="submit">Create User</Button>
                <Button type="button" variant="outline" onClick={() => setShowForm(false)}>Cancel</Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader><CardTitle>Users</CardTitle></CardHeader>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Role</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Last Login</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8">Loading...</TableCell></TableRow>
            ) : users.length === 0 ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8 text-text-secondary">No users found</TableCell></TableRow>
            ) : (
              users.map(user => (
                <TableRow key={user.id}>
                  <TableCell className="font-medium">{user.fullName}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell><Badge variant={roleVariant(user.role)}>{user.role}</Badge></TableCell>
                  <TableCell>
                    <Badge variant={user.isActive ? 'success' : 'secondary'}>
                      {user.isActive ? 'Active' : 'Disabled'}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-sm text-text-secondary">
                    {user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : 'Never'}
                  </TableCell>
                  <TableCell>
                    <Button size="sm" variant="outline" onClick={() => toggleActive(user)}>
                      {user.isActive ? 'Disable' : 'Enable'}
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </Card>
    </AppLayout>
  );
}
