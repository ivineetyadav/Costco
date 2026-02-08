'use client';

import { useEffect, useState } from 'react';
import AppLayout from '@/components/AppLayout';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { api } from '@/lib/api';
import { Plus, CheckCircle, XCircle } from 'lucide-react';

interface Device {
  id: string;
  machineId: string;
  machineName: string;
  deviceSerial: string;
  iotThingName: string;
  status: string;
  approvedBy: string;
  approvedAt: string;
  lastSeenAt: string;
  createdAt: string;
}

export default function DevicesPage() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ machineId: '', deviceSerial: '', iotThingName: '' });

  useEffect(() => { loadDevices(); }, []);

  const loadDevices = () => {
    api.get('/api/devices').then(setDevices).catch(console.error).finally(() => setLoading(false));
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/devices/register', form);
      setShowForm(false);
      setForm({ machineId: '', deviceSerial: '', iotThingName: '' });
      loadDevices();
    } catch (err: any) { alert(err.message); }
  };

  const handleApprove = async (id: string) => {
    try {
      await api.put(`/api/devices/${id}/approve`, {});
      loadDevices();
    } catch (err: any) { alert(err.message); }
  };

  const handleSuspend = async (id: string) => {
    try {
      await api.put(`/api/devices/${id}/suspend`, {});
      loadDevices();
    } catch (err: any) { alert(err.message); }
  };

  const statusVariant = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success' as const;
      case 'PENDING': return 'warning' as const;
      case 'SUSPENDED': return 'danger' as const;
      case 'DECOMMISSIONED': return 'secondary' as const;
      default: return 'secondary' as const;
    }
  };

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-primary">Devices</h1>
          <p className="text-sm text-text-secondary mt-1">IoT device management and approval</p>
        </div>
        <Button onClick={() => setShowForm(!showForm)}>
          <Plus className="h-4 w-4 mr-2" /> Register Device
        </Button>
      </div>

      {showForm && (
        <Card className="mb-6">
          <CardHeader><CardTitle>Register New Device</CardTitle></CardHeader>
          <CardContent>
            <form onSubmit={handleRegister} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input placeholder="Machine ID" value={form.machineId} onChange={e => setForm({...form, machineId: e.target.value})} />
              <Input placeholder="Device Serial (e.g. DEV-001)" value={form.deviceSerial} onChange={e => setForm({...form, deviceSerial: e.target.value})} required />
              <Input placeholder="IoT Thing Name" value={form.iotThingName} onChange={e => setForm({...form, iotThingName: e.target.value})} required />
              <div className="flex gap-2">
                <Button type="submit">Register</Button>
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
              <TableHead>Device Serial</TableHead>
              <TableHead>IoT Thing</TableHead>
              <TableHead>Machine</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Last Seen</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8">Loading...</TableCell></TableRow>
            ) : devices.length === 0 ? (
              <TableRow><TableCell colSpan={6} className="text-center py-8 text-text-secondary">No devices registered</TableCell></TableRow>
            ) : (
              devices.map(device => (
                <TableRow key={device.id}>
                  <TableCell className="font-mono text-sm">{device.deviceSerial}</TableCell>
                  <TableCell className="font-mono text-sm">{device.iotThingName}</TableCell>
                  <TableCell>{device.machineName || device.machineId || '-'}</TableCell>
                  <TableCell><Badge variant={statusVariant(device.status)}>{device.status}</Badge></TableCell>
                  <TableCell className="text-sm text-text-secondary">
                    {device.lastSeenAt ? new Date(device.lastSeenAt).toLocaleString() : 'Never'}
                  </TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      {device.status === 'PENDING' && (
                        <Button size="sm" variant="outline" onClick={() => handleApprove(device.id)}>
                          <CheckCircle className="h-3 w-3 mr-1" /> Approve
                        </Button>
                      )}
                      {device.status === 'ACTIVE' && (
                        <Button size="sm" variant="outline" onClick={() => handleSuspend(device.id)}>
                          <XCircle className="h-3 w-3 mr-1" /> Suspend
                        </Button>
                      )}
                    </div>
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
