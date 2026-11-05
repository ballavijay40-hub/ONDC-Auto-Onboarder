import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  UserCheck, 
  FileText, 
  Sparkles, 
  ShoppingBag, 
  Award,
  ExternalLink
} from 'lucide-react';

export const Sidebar = () => {
  const links = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/profile', label: 'Seller Profile', icon: UserCheck },
    { to: '/documents', label: 'Upload Documents', icon: FileText, badge: 'OCR' },
    { to: '/product-upload', label: 'AI Catalog Studio', icon: Sparkles, badge: 'AI' },
    { to: '/catalog', label: 'Products & Export', icon: ShoppingBag },
    { to: '/compliance', label: 'Compliance Audit', icon: Award },
  ];

  return (
    <aside className="w-64 border-r border-slate-200 bg-white flex flex-col justify-between shrink-0">
      <div className="p-4 space-y-6">
        <div>
          <p className="px-3 text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2">
            Onboarding Flow
          </p>
          <nav className="space-y-1">
            {links.map((link) => {
              const Icon = link.icon;
              return (
                <NavLink
                  key={link.to}
                  to={link.to}
                  className={({ isActive }) =>
                    `flex items-center justify-between rounded-xl px-3 py-2.5 text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-blue-50 text-blue-700 font-semibold shadow-sm'
                        : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                    }`
                  }
                >
                  <div className="flex items-center space-x-3">
                    <Icon className="h-4 w-4" />
                    <span>{link.label}</span>
                  </div>
                  {link.badge && (
                    <span className="rounded-full bg-blue-100 px-2 py-0.5 text-[10px] font-bold text-blue-700">
                      {link.badge}
                    </span>
                  )}
                </NavLink>
              );
            })}
          </nav>
        </div>
      </div>

      <div className="p-4 border-t border-slate-100">
        <div className="rounded-xl bg-gradient-to-br from-slate-900 to-blue-950 p-3.5 text-white shadow-md">
          <div className="flex items-center justify-between mb-1">
            <span className="text-[11px] font-bold uppercase tracking-wider text-blue-300">Network Standard</span>
            <span className="inline-block h-2 w-2 rounded-full bg-emerald-400 animate-pulse"></span>
          </div>
          <p className="text-xs font-semibold">ONDC Retail 1.2 Protocol</p>
          <p className="text-[11px] text-slate-300 mt-1">Fashion & Apparel Schema</p>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
