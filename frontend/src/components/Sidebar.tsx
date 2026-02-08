'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { cn } from '@/lib/utils';
import {
  LayoutDashboard,
  Wrench,
  Building2,
  FileText,
  Cpu,
  BarChart3,
  Receipt,
  Settings,
  LogOut,
} from 'lucide-react';

const navigation = [
  { name: 'Dashboard', href: '/dashboard', icon: LayoutDashboard, roles: ['ADMIN', 'MANAGER'] },
  { name: 'Machines', href: '/machines', icon: Wrench, roles: ['ADMIN', 'MANAGER', 'VENDOR'] },
  { name: 'Vendors', href: '/vendors', icon: Building2, roles: ['ADMIN', 'MANAGER'] },
  { name: 'Contracts', href: '/contracts', icon: FileText, roles: ['ADMIN', 'MANAGER', 'VENDOR'] },
  { name: 'Devices', href: '/devices', icon: Cpu, roles: ['ADMIN'] },
  { name: 'Reports', href: '/reports', icon: BarChart3, roles: ['ADMIN', 'MANAGER', 'VENDOR'] },
  { name: 'Billing', href: '/billing', icon: Receipt, roles: ['ADMIN', 'VENDOR'] },
  { name: 'Settings', href: '/settings', icon: Settings, roles: ['ADMIN'] },
];

export default function Sidebar() {
  const pathname = usePathname();
  const { user, logout } = useAuth();

  const filteredNav = navigation.filter(
    (item) => user && item.roles.includes(user.role)
  );

  return (
    <div className="flex flex-col w-60 bg-bg-subtle border-r border-border min-h-screen">
      <div className="flex items-center h-14 px-4 border-b border-border">
        <Wrench className="h-6 w-6 text-primary mr-2" />
        <span className="font-semibold text-text-primary">Costco Mining</span>
      </div>

      <nav className="flex-1 px-3 py-4 space-y-1">
        {filteredNav.map((item) => {
          const isActive = pathname.startsWith(item.href);
          return (
            <Link
              key={item.name}
              href={item.href}
              className={cn(
                'flex items-center px-3 py-2 text-sm rounded-md transition-colors',
                isActive
                  ? 'bg-primary/10 text-primary font-medium'
                  : 'text-text-secondary hover:text-text-primary hover:bg-white'
              )}
            >
              <item.icon className="h-4 w-4 mr-3" />
              {item.name}
            </Link>
          );
        })}
      </nav>

      <div className="px-3 py-4 border-t border-border">
        <div className="px-3 py-2 text-xs text-text-secondary mb-2">
          {user?.fullName}
          <br />
          <span className="text-xs opacity-75">{user?.role}</span>
        </div>
        <button
          onClick={logout}
          className="flex items-center w-full px-3 py-2 text-sm text-text-secondary hover:text-red-600 rounded-md hover:bg-white transition-colors"
        >
          <LogOut className="h-4 w-4 mr-3" />
          Logout
        </button>
      </div>
    </div>
  );
}
