import React from 'react';
import { useAuth } from '../context/AuthContext';
import { LogOut, User, ShieldCheck } from 'lucide-react';

export const Navbar = () => {
  const { seller, logout } = useAuth();

  return (
    <header className="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b border-slate-200 bg-white/80 px-6 backdrop-blur-md">
      <div className="flex items-center space-x-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-blue-700 to-indigo-600 text-white shadow-md shadow-blue-500/20">
          <ShieldCheck className="h-6 w-6" />
        </div>
        <div>
          <h1 className="text-base font-bold text-slate-900 tracking-tight">
            ONDC <span className="text-blue-600">Auto-Onboarder</span>
          </h1>
          <p className="text-xs text-slate-500">Fashion & Apparel Seller Acceleration</p>
        </div>
      </div>

      <div className="flex items-center space-x-4">
        {seller && (
          <div className="flex items-center space-x-3 border-r border-slate-200 pr-4">
            <div className="h-9 w-9 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-semibold text-sm">
              {seller.ownerName ? seller.ownerName.charAt(0).toUpperCase() : 'S'}
            </div>
            <div className="text-right">
              <p className="text-sm font-semibold text-slate-800 leading-none">
                {seller.businessName || 'My Business'}
              </p>
              <p className="text-xs text-slate-500 mt-0.5">{seller.email}</p>
            </div>
          </div>
        )}

        <button
          onClick={logout}
          className="inline-flex items-center space-x-1.5 rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50 hover:text-red-600 transition-colors shadow-sm"
          title="Sign out"
        >
          <LogOut className="h-3.5 w-3.5" />
          <span>Logout</span>
        </button>
      </div>
    </header>
  );
};

export default Navbar;
